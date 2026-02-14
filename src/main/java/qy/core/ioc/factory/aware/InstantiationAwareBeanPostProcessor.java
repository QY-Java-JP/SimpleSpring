package qy.core.ioc.factory.aware;

import qy.core.ioc.factory.BeanPostProcessor;
import qy.exception.bean.BeanCreateException;

import java.util.Map;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    @Override
    default Object postProcessorAfterInit(Object bean, String beanName) throws BeanCreateException {
        return bean;
    }

    // 这里可以修改属性
    void postProcessorProcessor(Map<String, Object> propValueMap, Object bean, String beanName)
            throws BeanCreateException;

    @Override
    default Object postProcessorBeforeInit(Object bean, String beanName) throws BeanCreateException {
        return bean;
    }
}
