package io.github.qy.core.ioc.factory.aware;

import io.github.qy.core.ioc.factory.BeanPostProcessor;

import java.util.List;

public interface BeanPostProcessorAware {
    void setBeanPostProcessorList(List<BeanPostProcessor> processors);
}
