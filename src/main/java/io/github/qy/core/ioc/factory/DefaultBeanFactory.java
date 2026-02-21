package io.github.qy.core.ioc.factory;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import io.github.qy.bean.ioc.BeanDefinition;
import io.github.qy.bean.ioc.BeanWrapper;
import io.github.qy.core.ioc.factory.aware.BeanFactoryAware;
import io.github.qy.core.ioc.factory.aware.BeanPostProcessorAware;
import io.github.qy.core.ioc.factory.aware.InstantiationAwareBeanPostProcessor;
import io.github.qy.core.ioc.factory.aware.RunClassAware;
import io.github.qy.exception.bean.BeanCreateException;
import io.github.qy.util.RefUtil;
import io.github.qy.util.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultBeanFactory implements BeanFactory {

    //===============================容器=================================================
    // 最终bean容器 (Key: beanName value: bean)
    private final Map<String, Object> completeBeanMap = new ConcurrentHashMap<>();
    // 早期bean容器 (key: beanName value: bean)
    private final Map<String, Object> earlyBeanMap = new ConcurrentHashMap<>();
    // 工厂bean容器
    private final Map<String, ObjectFactory<?>> beanFactoryMap = new ConcurrentHashMap<>();

    // bean描述
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    // bean类型对应的beanName
    private final Map<Class<?>, List<String>> beanNameByTypeMap = new ConcurrentHashMap<>();

    // bean后置处理器
    private final List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    //================================其他变量==============================================
    // 启动类
    private final Class<?> runClass;

    //================================锁===================================================
    private final Object getCompleteLock = new Object();

    public DefaultBeanFactory(Class<?> runClass) {
        this.runClass = runClass;
    }

    @Override
    @Nullable
    public Object getBean(String beanName) {
        return doGetBean(beanName);
    }

    @Override
    public List<Object> getBean(Class<?> beanType) {
        List<String> beanNames = beanNameByTypeMap.get(beanType);
        if (beanNames == null) {
            reflushNameTypeMapOneType(beanType);
            beanNames = beanNameByTypeMap.get(beanType);
        }

        final List<Object> beans = new ArrayList<>(beanNames.size());
        beanNames.forEach(n -> beans.add(doGetBean(n)));

        return beans;
    }

    @Override
    @Nullable
    public Object getSingleBean(Class<?> beanType) {
        final List<Object> beans = getBean(beanType);
        return beans.isEmpty() ? null : beans.get(0);
    }

    @Override
    public int getTypeBeanSize(Class<?> beanType) {
        List<String> names = beanNameByTypeMap.get(beanType);
        if (names == null) {
            reflushNameTypeMapOneType(beanType);
            names = beanNameByTypeMap.get(beanType);
        }

        return names == null ? 0 : names.size();
    }

    @Override
    public boolean containBean(Class<?> beanType) {
        return beanNameByTypeMap.containsKey(beanType);
    }

    @Override
    public boolean containBean(String beanName) {
        return completeBeanMap.containsKey(beanName);
    }

    @Override
    public void registerBeanDefinition(BeanDefinition definition) throws BeanCreateException{
        if (beanDefinitionMap.containsKey(definition.getBeanName())) {
            throw new BeanCreateException(definition.getBeanName() + "已存在");
        }
        if (completeBeanMap.containsKey(definition.getBeanName())) {
            throw new BeanCreateException(definition.getBeanName() + " 已存在");
        }

        beanDefinitionMap.put(definition.getBeanName(), definition);
        beanNameByTypeMap.remove(definition.getBeanType());
    }

    @Override
    public void registerCompleteBean(String beanName, Object bean) throws BeanCreateException{
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new BeanCreateException(beanName + "已存在");
        }
        if (completeBeanMap.containsKey(beanName)) {
            throw new BeanCreateException(beanName + " 已存在");
        }

        completeBeanMap.put(beanName, bean);
        beanNameByTypeMap.remove(bean.getClass());
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor processor) {
        beanPostProcessorList.add(processor);
    }

    @Override
    public List<String> getBeanNames(Class<?> beanType) {
        List<String> names = beanNameByTypeMap.get(beanType);
        if (names == null) {
            reflushNameTypeMapOneType(beanType);
            names = beanNameByTypeMap.get(beanType);
        }

        return names == null ? new ArrayList<>() : names;
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

    // 获取bean
    @Nullable
    private Object doGetBean(String beanName){
        // 拿缓存
        Object bean = getBeanAnyway(beanName, true);
        if (bean != null) return bean;

        final BeanDefinition bd = beanDefinitionMap.get(beanName);
        if (bd == null) return null;

        return doCreateBean(bd);
    }

    // 构造bean
    private Object doCreateBean(BeanDefinition bd){
        // 我们目前不考虑其他构造方式 我们只搞无参构造
        Object bean = newBeanByDefinition(bd);
        // 后置修改bean描述
        applyMergedBdPostProcessor(bd);

        BeanWrapper bw = new BeanWrapper(bean);
        log.debug("bean:{} 无参构造构造完毕", bd.getBeanName());

        // aware
        applyAwareByBeforeInit(bw);

        // 暴漏到三级工厂
        final BeanWrapper finalBw = bw;
        beanFactoryMap.put(bd.getBeanName(), () -> applyEarlyBeanPostProcessor(finalBw, bd));

        // 依赖注入
        applyInstantiationProcessor(bw, bd);

        // 前置处理&init&后置处理
        Object initBean = initializeBean(bd, bw.getBean());

        // 注意: 在依赖注入的时候 若其他对象要使用这个对象会触发applyEarlyBeanPostProcessor() 那么会生成一个新的对象
        // 此时这个对象在二级缓存里 那么如果这个对象有改变比如进行了AOP 这会导致当前bw里的bean和二级缓存的bean不是一个
        // 若冒然进行正常操作 会导致不一致的现象 所以我们要再一次getBean检查 如果拿到的bean和当前bw的不是同一个则使用缓存里的
        final Object maybeAopBean = getBeanAnyway(bd.getBeanName(), false);
        if (maybeAopBean != null) {
            // 这代表着有人使用了三级工厂 当前的bw的bean已经不是最新的了 我们需要更新
            // 但是我们还需要知道 initBean之后和之前是不是同一个 如果出现被aop了也被后置处理器改了 那么这是处理不了的情况
            if (bw.getBean() != initBean) {
                throw new BeanCreateException("beanName: " + bd.getBeanName() + "出现了早期引用和bean后置处理器状态不一致的现象"
                    + " 比如这个bean进行了AOP的同时又在某个后置处理器中被修改了引用");
            }

            initBean = maybeAopBean;
        }
        bw = new BeanWrapper(initBean);

        // 这个时候已经完成了整个创建的生命周期 bean有可能在二级缓存也有可能在三级缓存 我们都移动到一级缓存来
        completeBeanMap.put(bd.getBeanName(), bw.getBean());
        earlyBeanMap.remove(bd.getBeanName());
        beanFactoryMap.remove(bd.getBeanName());

        return completeBeanMap.get(bd.getBeanName());
    }

    // 构造一个对象
    private Object newBeanByDefinition(BeanDefinition definition){
        final Constructor<?> emptyCons = RefUtil.findConstructor(definition.getBeanType());
        if (emptyCons == null) {
            throw new BeanCreateException("bean:" + definition.getBeanName() + "没有无参构造");
        }

        try {
            return emptyCons.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // 执行bean后置处理器以及init
    private Object initializeBean(BeanDefinition bd, Object wrapperBean){
        Object initBean = wrapperBean;
        // 前置处理
        initBean = applyBeforePostProcessor(bd, initBean);
        // init
        applyInit(bd, initBean);
        // 后置处理
        initBean = applyAfterPostProcessor(bd, initBean);

        return initBean;
    }

    // 执行前置处理
    private Object applyBeforePostProcessor(BeanDefinition bd, Object wrapperBean){
        Object overBean = wrapperBean;
        for (BeanPostProcessor processor : beanPostProcessorList) {
            overBean = processor.postProcessorBeforeInit(overBean, bd.getBeanName());
            if (overBean == null) {
                return null;
            }
        }

        return overBean;
    }

    // 执行init方法
    private void applyInit(BeanDefinition bd, Object wrapperBean){
        if (!StringUtil.hasText(bd.getInitMethodName())) return;

        RefUtil.runMethod(wrapperBean, bd.getInitMethodName());
        log.debug("bean:{} 执行init完毕", bd.getBeanName());
    }

    // 执行后置处理
    private Object applyAfterPostProcessor(BeanDefinition bd, Object wrapperBean){
        Object overBean = wrapperBean;
        for (BeanPostProcessor processor : beanPostProcessorList) {
            overBean = processor.postProcessorAfterInit(overBean, bd.getBeanName());
            if (overBean == null) {
                return null;
            }
        }

        return overBean;
    }

    // 获取bean
    @Nullable
    private Object getBeanAnyway(String beanName, boolean useFactory) throws BeanCreateException {
        // 先从一级缓存找 再从二级 实在不行新建
        Object bean = completeBeanMap.get(beanName);
        if (bean != null) return bean;

        // 二级缓存如果没有且不能新建 直接走
        bean = earlyBeanMap.get(beanName);
        if (bean != null || !useFactory) return bean;

        // 加锁新建
        synchronized (getCompleteLock) {
            bean = completeBeanMap.get(beanName);
            if (bean != null) return bean;

            bean = earlyBeanMap.get(beanName);
            if (bean != null) return bean;

            ObjectFactory<?> factory = beanFactoryMap.remove(beanName);
            if (factory == null) return null;

            bean = factory.getBean();
            earlyBeanMap.put(beanName, bean);
        }

        return bean;
    }

    // 调用依赖注入的后置处理器
    private void applyInstantiationProcessor(BeanWrapper bw, BeanDefinition bd){
        for (BeanPostProcessor processor : beanPostProcessorList) {
            if (!(processor instanceof InstantiationAwareBeanPostProcessor)) continue;

            InstantiationAwareBeanPostProcessor insProcessor = (InstantiationAwareBeanPostProcessor) processor;
            insProcessor.postProcessorProcessor(bd.getPropValueMap(), bw.getBean(), bd.getBeanName());
        }

        // 注入进去
        Map<String, Object> propMap = bd.getPropValueMap();
        for (String propName : propMap.keySet()) {
            RefUtil.setProp(bw.getBean(), propName, propMap.get(propName));
            log.debug("bean:{} 属性:{} 注入:{}", bd.getBeanName(), propName, propMap.get(propName));
        }
    }

    // 调用EarlyBeanPostProcessor 获取早期bean
    private Object applyEarlyBeanPostProcessor(BeanWrapper bw, BeanDefinition bd){
        Object aopBean = bw.getBean();
        for (BeanPostProcessor processor : beanPostProcessorList) {
            if (!(processor instanceof EarlyBeanPostProcessor)) continue;

            EarlyBeanPostProcessor earlyProcessor = (EarlyBeanPostProcessor) processor;
            aopBean = earlyProcessor.getEarlyBeanReference(aopBean, bd.getBeanName());
        }

        return aopBean;
    }

    // 调用修改bean描述的处理器
    private void applyMergedBdPostProcessor(BeanDefinition bd){
        for (BeanPostProcessor processor : beanPostProcessorList) {
            if (!(processor instanceof MergedBeanDefinitionPostProcessor)) continue;

            MergedBeanDefinitionPostProcessor mergedProcessor = (MergedBeanDefinitionPostProcessor) processor;
            mergedProcessor.postProcessMergedBeanDefinition(bd);
        }
    }

    // 调用aware系列接口
    private void applyAwareByBeforeInit(BeanWrapper bw){
        // BeanFactory
        final Object bean = bw.getBean();
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
        // BeanPostProcessor
        if (bean instanceof BeanPostProcessorAware) {
            ((BeanPostProcessorAware) bean).setBeanPostProcessorList(beanPostProcessorList);
        }
        // RunClassAware
        if (bean instanceof RunClassAware) {
            ((RunClassAware) bean).setRunClass(runClass);
        }
    }

    // 刷新beanNameType集合
    private void reflushNameTypeMapOneType(Class<?> type){
        // 不同type之间其实有父子关系 所有不能只清理type的
        for (Class<?> clazz : beanNameByTypeMap.keySet()) {
            // 如果没有父子关系 跳过
            if (!type.isAssignableFrom(clazz)) continue;
            beanNameByTypeMap.remove(clazz);
        }

        // 整理好新的
        final List<String> names = new ArrayList<>();
        for (BeanDefinition bd : beanDefinitionMap.values()) {
            if (type.isAssignableFrom(bd.getBeanType())) {
                names.add(bd.getBeanName());
            }
        }

        beanNameByTypeMap.put(type, names);
    }
}
