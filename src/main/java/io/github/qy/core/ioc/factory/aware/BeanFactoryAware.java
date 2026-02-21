package io.github.qy.core.ioc.factory.aware;

import io.github.qy.core.ioc.factory.BeanFactory;

public interface BeanFactoryAware {
    void setBeanFactory(BeanFactory factory);
}
