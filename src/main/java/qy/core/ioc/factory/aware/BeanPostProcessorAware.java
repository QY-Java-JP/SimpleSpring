package qy.core.ioc.factory.aware;

import qy.core.ioc.factory.BeanPostProcessor;

import java.util.List;

public interface BeanPostProcessorAware {
    void setBeanPostProcessorList(List<BeanPostProcessor> processors);
}
