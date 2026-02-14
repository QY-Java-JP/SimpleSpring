package qy.core.ioc.factory;

import qy.bean.ioc.BeanDefinition;
import qy.exception.bean.BeanCreateException;

public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor{

    @Override
    default Object postProcessorAfterInit(Object bean, String beanName) throws BeanCreateException {
        return bean;
    }

    @Override
    default Object postProcessorBeforeInit(Object bean, String beanName) throws BeanCreateException {
        return bean;
    }

    // 这里可以修改bean描述
    void postProcessMergedBeanDefinition(BeanDefinition bd);
}
