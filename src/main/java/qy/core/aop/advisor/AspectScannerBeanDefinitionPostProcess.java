package qy.core.aop.advisor;

import qy.bean.ioc.BeanDefinition;
import qy.core.ioc.BeanDefinitionPostProcessor;
import qy.core.ioc.factory.BeanFactory;
import qy.exception.bean.BeanDefinitionCreateException;

import java.util.List;
import java.util.Map;

public class AspectScannerBeanDefinitionPostProcess implements BeanDefinitionPostProcessor {

    // bean工厂
    private final BeanFactory beanFactory;
    // 所有切面
    private final List<Aspect> aspectList;

    public AspectScannerBeanDefinitionPostProcess(BeanFactory beanFactory, List<Aspect> aspectList) {
        this.beanFactory = beanFactory;
        this.aspectList = aspectList;
    }

    @Override
    public void postProcessorBeanDefinition(Map<String, BeanDefinition> definitionMap) throws BeanDefinitionCreateException {
        // 拿到AbsMethodInvocationAdvice的bean
        final List<Object> advices = beanFactory.getBean(AbsMethodInvocationAdvice.class);
        for (Object adviceObj : advices) {
            AbsMethodInvocationAdvice advice = (AbsMethodInvocationAdvice) adviceObj;
            aspectList.add(new Aspect(advice.getPointCut(), advice));
        }
    }
}
