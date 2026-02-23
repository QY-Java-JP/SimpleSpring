package io.github.qy.core.ioc.processor;

import lombok.extern.slf4j.Slf4j;
import io.github.qy.annotation.ioc.Bean;
import io.github.qy.annotation.ioc.Configurable;
import io.github.qy.bean.ioc.BeanDefinition;
import io.github.qy.core.ioc.BeanDefinitionPostProcessor;
import io.github.qy.core.ioc.factory.BeanFactory;
import io.github.qy.exception.bean.BeanDefinitionCreateException;
import io.github.qy.util.BeanUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConfigurableBeanFactoryPostProcessor implements BeanDefinitionPostProcessor {

    private BeanFactory beanFactory;

    public ConfigurableBeanFactoryPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessorBeanDefinition(Map<String, BeanDefinition> definitionMap)
            throws BeanDefinitionCreateException {
        // 拿到有@Confifurable注解的类
        final List<BeanDefinition> configClassList = definitionMap.values()
                .stream()
                .filter(bd -> BeanUtil.hasThisAnnotation(bd.getBeanType(), Configurable.class))
                .toList();

        // 遍历有@Bean的方法
        for (BeanDefinition bd : configClassList) {
            Object configBean = beanFactory.getBean(bd.getBeanName());
            List<Method> methods = BeanUtil.findHasThisAnnotationMethods(bd.getBeanType(), Bean.class);
            for (Method m : methods) {
                Object methodBean = createBeanFromBeanMethod(configBean, bd.getBeanName(), m);
                beanFactory.registerBeanDefinition(new BeanDefinition(m.getName(), methodBean.getClass()));
                beanFactory.registerCompleteBean(m.getName(), methodBean);
            }
        }
    }

    // 根据一个含有@Bean的方法拿到bean
    // TODO Spring的方案是创建bean描述 但是设置factoryBean factoryMethod 之后还是调用这个方法创建 我们没有这么复杂直接从这里创建即可
    private Object createBeanFromBeanMethod(Object configBean, String configBeanName, Method method)
            throws BeanDefinitionCreateException{
        // 我们要考虑依赖注入的问题 但是不学Spring的复杂的推断构造 我们就拿到形参列表里的参数即可
        // 若有这个参数则注入 多个则注入第一个 没有注入null
        final Parameter[] parameters = method.getParameters();
        final Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            Object arg = beanFactory.getSingleBean(p.getType());
            if (arg == null) {
                log.warn("解析配置类:{} 的方法:{} 时参数:{} 对应的类型未找到注入null", configBeanName, method.getName(), p.getName());
            }
            args[i] = arg;
        }

        // 调用方法拿到返回值
        Object invokeBean;
        try {
            invokeBean = method.invoke(configBean, args);
            if (invokeBean == null) {
                throw new BeanDefinitionCreateException("配置类:"+ configBeanName +" 方法:"+
                        method.getName() +" 返回值不可为null");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanDefinitionCreateException(e.getMessage());
        }

        return invokeBean;
    }

}
