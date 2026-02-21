package io.github.qy.core.ioc.factory;

import io.github.qy.exception.bean.BeanCreateException;

public interface EarlyBeanPostProcessor extends BeanPostProcessor{

    @Override
    default Object postProcessorBeforeInit(Object bean, String beanName) throws BeanCreateException {
        return bean;
    }

    @Override
    default Object postProcessorAfterInit(Object bean, String beanName) throws BeanCreateException {
        return bean;
    }

    // 第三级缓存调用 可以用于早期aop
    Object getEarlyBeanReference(Object bean, String beanName) throws BeanCreateException;
}
