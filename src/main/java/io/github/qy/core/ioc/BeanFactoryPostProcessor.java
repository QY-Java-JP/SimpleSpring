package io.github.qy.core.ioc;

import io.github.qy.core.ioc.factory.BeanFactory;
import io.github.qy.exception.bean.BeanException;

@FunctionalInterface
public interface BeanFactoryPostProcessor {
    void postProcessorBeanFactory(BeanFactory factory) throws BeanException;
}
