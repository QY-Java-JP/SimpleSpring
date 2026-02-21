package io.github.qy.core.aop.advisor;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import io.github.qy.core.ioc.factory.EarlyBeanPostProcessor;
import io.github.qy.exception.bean.BeanCreateException;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class AopInterceptorBeanPostProcessor implements EarlyBeanPostProcessor {

    public static final String BEAN_NAME = "AopInterceptorBeanPostProcessor";

    // 所有切面
    private final List<Aspect> aspectList;
    // 被代理过的bean
    private final Set<String> proxiedBeans = new HashSet<>();

    public AopInterceptorBeanPostProcessor(List<Aspect> aspectList) {
        this.aspectList = aspectList;
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeanCreateException {
        proxiedBeans.add(beanName);
        return wrapIfNecessary(bean, beanName);
    }

    @Override
    public Object postProcessorAfterInit(Object bean, String beanName) throws BeanCreateException {
        // 看看有没有被代理过 没有就代理
        if (proxiedBeans.contains(beanName)) return bean;

        proxiedBeans.add(beanName);
        return wrapIfNecessary(bean, beanName);
    }

    // 代理
    private Object wrapIfNecessary(Object bean, String beanName){
        // 遍历每个方法 整理出所有方法对应的切片
        final Class<?> beanClass = bean.getClass();
        final Map<Method, List<Advice>> methodAdviceMap = new HashMap<>();
        for (Method method : beanClass.getMethods()) {
            List<Advice> advices = createMethodAdvices(method);
            if (!advices.isEmpty()) {
                methodAdviceMap.put(method, advices);
            }
        }

        // 如果发现没有任何代理 直接返回原始bean即可
        if (methodAdviceMap.isEmpty()) return bean;

        // 生成代理返回
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanClass);
        enhancer.setCallback(new AopInterceptor(bean, methodAdviceMap));

        log.debug("bean:{} 生成cglib代理对象", beanName);

        return enhancer.create();
    }

    // 生成方法对应的切片
    private List<Advice> createMethodAdvices(Method method){
        // 遍历所有切点即可
        final List<Advice> advices = new ArrayList<>();
        for (Aspect aspect : aspectList) {
            if (aspect.getPointCut().needCut(method)) {
                advices.add(aspect.getAdvice());
            }
        }

        return advices;
    }

}
