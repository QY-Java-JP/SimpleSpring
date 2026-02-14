package qy.core.ioc;

import qy.core.ioc.factory.BeanFactory;
import qy.exception.bean.BeanException;

@FunctionalInterface
public interface BeanFactoryPostProcessor {
    void postProcessorBeanFactory(BeanFactory factory) throws BeanException;
}
