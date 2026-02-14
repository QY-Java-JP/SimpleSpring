package qy.core.ioc.factory.aware;

import qy.core.ioc.factory.BeanFactory;

public interface BeanFactoryAware {
    void setBeanFactory(BeanFactory factory);
}
