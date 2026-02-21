package io.github.qy.core.ioc;

import io.github.qy.bean.ioc.BeanDefinition;
import io.github.qy.core.ioc.factory.BeanFactory;
import io.github.qy.exception.bean.BeanDefinitionCreateException;
import io.github.qy.exception.bean.BeanException;

import java.util.Map;

public interface BeanDefinitionPostProcessor extends BeanFactoryPostProcessor {

    @Override
    default void postProcessorBeanFactory(BeanFactory factory) throws BeanException {}

    void postProcessorBeanDefinition(Map<String, BeanDefinition> definitionMap) throws BeanDefinitionCreateException;
}
