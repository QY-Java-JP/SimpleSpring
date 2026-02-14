package qy.core.ioc;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import qy.bean.ioc.BeanDefinition;
import qy.core.aop.advisor.AopInterceptorBeanPostProcessor;
import qy.core.aop.advisor.Aspect;
import qy.core.aop.advisor.AspectScannerBeanDefinitionPostProcess;
import qy.core.ioc.factory.BeanFactory;
import qy.core.ioc.factory.BeanPostProcessor;
import qy.core.ioc.factory.DefaultBeanFactory;
import qy.core.ioc.processor.AutowiredBeanPostProcessor;
import qy.core.ioc.processor.ComponentBeanFactoryPostProcessor;
import qy.core.ioc.processor.ConfigurableBeanFactoryPostProcessor;
import qy.exception.bean.BeanCreateException;
import qy.util.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ApplicationContext implements BeanFactory {

    // bean工厂
    private final BeanFactory beanFactory;
    // bean工厂的后置处理器
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    // 启动类class
    private final Class<?> runApplicationClass;

    ApplicationContext(Class<?> runClass){
        this.runApplicationClass = runClass;
        this.beanFactory = new DefaultBeanFactory(runClass);

        // 注册默认的bean
        beanFactoryPostProcessors.add(new ComponentBeanFactoryPostProcessor(runClass));
        beanFactoryPostProcessors.add(new ConfigurableBeanFactoryPostProcessor(beanFactory));

        beanFactory.registerBeanDefinition(BeanUtil.createBeanDefinition(AutowiredBeanPostProcessor.class,
                AutowiredBeanPostProcessor.BEAN_NAME));

        final List<Aspect> allAspect = new ArrayList<>();
        beanFactoryPostProcessors.add(new AspectScannerBeanDefinitionPostProcess(beanFactory, allAspect));
        beanFactory.addBeanPostProcessor(new AopInterceptorBeanPostProcessor(allAspect));

        refresh();
    }

    public void refresh(){
        // 调用bean工厂的后置处理器 收集bean描述
        log.info("开始执行beanFactoryPostProcessor");
        applyBeanFactoryPostProcessor();

        // 生产所有bean的后置处理器
        log.info("开始生产bean后置处理器");
        createAllBeanPostProcess();

        // 生产所有单例bean
        log.info("开始建造所有bean");
        createAllBeanDefinitionBean();
    }

    // 调用所有bean工厂的后置处理器
    private void applyBeanFactoryPostProcessor(){
        // 首先把我们自己规定的后置处理器执行了
        int nowProcessorSize = beanFactory.getTypeBeanSize(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor processor : beanFactoryPostProcessors) {
            applyBeanFactoryProcessor0(processor);
        }

        // 接下来是经典的操作 用while来检查是否有新的工厂后置处理器 如果有再来
        // 千世乃至万世
        while (beanFactory.getTypeBeanSize(BeanFactoryPostProcessor.class) > nowProcessorSize) {
            nowProcessorSize = beanFactory.getTypeBeanSize(BeanFactoryPostProcessor.class);
            final List<Object> processors = beanFactory.getBean(BeanFactoryPostProcessor.class);
            final List<BeanFactoryPostProcessor> readyApplyProcessors = new ArrayList<>();
            for (Object processorObj : processors) {
                // 如果这个已经处理过了 则跳过
                final BeanFactoryPostProcessor processor = (BeanFactoryPostProcessor) processorObj;
                if (beanFactoryPostProcessors.contains(processor)) continue;

                // 加入到集合中
                readyApplyProcessors.add(processor);
            }

            // 排序 执行
            BeanUtil.sortByOrder(readyApplyProcessors);
            readyApplyProcessors.forEach(p -> {
                applyBeanFactoryProcessor0(p);
                beanFactoryPostProcessors.add(p);
            });
            readyApplyProcessors.clear();
        }
    }

    // 调用bean工厂后置处理器以及子类
    private void applyBeanFactoryProcessor0(BeanFactoryPostProcessor processor){
        processor.postProcessorBeanFactory(beanFactory);
        // BeanDefinitionPostProcessor
        if (processor instanceof BeanDefinitionPostProcessor) {
            ((BeanDefinitionPostProcessor) processor).postProcessorBeanDefinition(beanFactory.getBeanDefinitionMap());
        }
    }

    // 生产所有bean的后置处理器
    private void createAllBeanPostProcess(){
        final List<String> processorList = beanFactory.getBeanNames(BeanPostProcessor.class);

        // 因为要排序 所以我们要先在这里存一份
        final List<BeanPostProcessor> sortProcessorList = new ArrayList<>();
        processorList.forEach(n -> sortProcessorList.add((BeanPostProcessor) beanFactory.getBean(n)));

        BeanUtil.sortByOrder(sortProcessorList);
        sortProcessorList.forEach(beanFactory::addBeanPostProcessor);
    }

    // 生产所有bean描述的bean
    private void createAllBeanDefinitionBean(){
        final List<String> beanNames = beanFactory.getBeanNames(Object.class);
        for (String beanName : beanNames) {
            beanFactory.getBean(beanName);
        }
    }

    @Override
    @Nullable
    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    @Override
    public List<Object> getBean(Class<?> beanType) {
        return beanFactory.getBean(beanType);
    }

    @Override
    @Nullable
    public Object getSingleBean(Class<?> beanType) {
        return beanFactory.getSingleBean(beanType);
    }

    @Override
    public int getTypeBeanSize(Class<?> beanType) {
        return beanFactory.getTypeBeanSize(beanType);
    }

    @Override
    public boolean containBean(Class<?> beanType) {
        return beanFactory.containBean(beanType);
    }

    @Override
    public boolean containBean(String beanName) {
        return beanFactory.containBean(beanName);
    }

    @Override
    public void registerBeanDefinition(BeanDefinition definition) throws BeanCreateException {
        beanFactory.registerBeanDefinition(definition);
    }

    @Override
    public void registerCompleteBean(String beanName, Object bean) throws BeanCreateException {
        beanFactory.registerCompleteBean(beanName, bean);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor processor) {
        beanFactory.addBeanPostProcessor(processor);
    }

    @Override
    public List<String> getBeanNames(Class<?> beanType) {
        return beanFactory.getBeanNames(beanType);
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanFactory.getBeanDefinitionMap();
    }
}
