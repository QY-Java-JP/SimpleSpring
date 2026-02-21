package io.github.qy.core.ioc.factory;

import io.github.qy.exception.bean.BeanCreateException;

public interface ObjectFactory<T> {
    T getBean() throws BeanCreateException;
}
