package io.github.qy.core.ioc.factory;

import org.jetbrains.annotations.Nullable;
import io.github.qy.bean.ioc.BeanDefinition;
import io.github.qy.exception.bean.BeanCreateException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public interface BeanFactory {
    // 获取bean
    @Nullable
    Object getBean(String beanName);

    // 获取Bean
    List<Object> getBean(Class<?> beanType);

    // 获取bean
    @Nullable
    Object getSingleBean(Class<?> beanType);

    // 获取某个bean类型的数量
    int getTypeBeanSize(Class<?> beanType);

    // 是否存在这个bean
    boolean containBean(Class<?> beanType);

    // 是否存在这个bean
    boolean containBean(String beanName);

    // 注册bean描述
    void registerBeanDefinition(BeanDefinition definition) throws BeanCreateException;

    // 注册bean
    void registerCompleteBean(String beanName, Object bean) throws BeanCreateException;

    // 注册bean的后置处理器
    void addBeanPostProcessor(BeanPostProcessor processor);

    // 获取一个类型的bean名字
    List<String> getBeanNames(Class<?> beanType);

    // 获取所有bean描述
    Map<String, BeanDefinition> getBeanDefinitionMap();

    /**
     * 解析一个依赖需求
     * @param dependencyType 需要解析的
     * @param beanName 需要获得解析需求的bean的名字
     * @param autowiredBeanName 解析bean在@Autowired中指定的名字
     * @return 解析到的值 可能是一个Object 可能是List 可能是Map
     */
    @Nullable
    Object resolveDependency(Type dependencyType, String beanName, @Nullable String autowiredBeanName);
}