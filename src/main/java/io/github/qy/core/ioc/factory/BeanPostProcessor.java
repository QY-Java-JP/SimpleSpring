package io.github.qy.core.ioc.factory;

import org.jetbrains.annotations.Nullable;
import io.github.qy.exception.bean.BeanCreateException;

public interface BeanPostProcessor {

    // 初始化前
    @Nullable
    Object postProcessorBeforeInit(Object bean, String beanName) throws BeanCreateException;

    // 初始化后
    @Nullable
    Object postProcessorAfterInit(Object bean, String beanName) throws BeanCreateException;
}
