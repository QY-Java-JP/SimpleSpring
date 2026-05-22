package io.github.qy.core.ioc;

import io.github.qy.annotation.ioc.Autowired;
import io.github.qy.bean.ioc.BeanDefinition;
import io.github.qy.bean.ioc.BeanWrapper;
import io.github.qy.core.ioc.factory.BeanFactory;
import io.github.qy.exception.bean.BeanCreateException;
import io.github.qy.util.BeanUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

public class ConstructorResolver {
    // bean工厂
    private final BeanFactory beanFactory;

    public ConstructorResolver(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    // 根据选出的构造构造出这个对象
    public BeanWrapper autowireConstructor(BeanDefinition bd, Constructor<?> selectedCons, @Nullable Object[] args) {
        // 如果有参数则不需要找参数
        if (args == null) {
            args = findConstructorArgs(selectedCons, bd.getBeanName());
        }

        // 然后赋值即可
        Object bean;
        try {
            bean = selectedCons.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreateException(e.getMessage());
        }

        return new BeanWrapper(bean);
    }

    // 找出一个构造方法的参数
    private Object[] findConstructorArgs(Constructor<?> cons, String beanName){
        // 一个个从bean工厂里获取即可
        final Type[] parameterTypes = cons.getGenericParameterTypes();
        final Parameter[] parameters = cons.getParameters();

        Object[] retArray = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            // 关于这个参数的beanName 这个参数可能被@Autowirted注解 如果被注解了就是这个 如果没有则类名小写即可
            Parameter parameter = parameters[i];
            Autowired autowired = parameter.getAnnotation(Autowired.class);
            retArray[i] = beanFactory.resolveDependency(parameterTypes[i], beanName, autowired == null ?
                    BeanUtil.getDefaultBeanName(parameter.getType()) : autowired.value());
        }

        return retArray;
    }

}
