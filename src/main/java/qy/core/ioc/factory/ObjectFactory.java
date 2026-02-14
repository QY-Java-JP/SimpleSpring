package qy.core.ioc.factory;

import qy.exception.bean.BeanCreateException;

public interface ObjectFactory<T> {
    T getBean() throws BeanCreateException;
}
