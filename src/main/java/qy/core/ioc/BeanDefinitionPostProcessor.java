package qy.core.ioc;

import qy.bean.ioc.BeanDefinition;
import qy.core.ioc.factory.BeanFactory;
import qy.exception.bean.BeanDefinitionCreateException;
import qy.exception.bean.BeanException;

import java.util.Map;

public interface BeanDefinitionPostProcessor extends BeanFactoryPostProcessor {

    @Override
    default void postProcessorBeanFactory(BeanFactory factory) throws BeanException {}

    void postProcessorBeanDefinition(Map<String, BeanDefinition> definitionMap) throws BeanDefinitionCreateException;
}
