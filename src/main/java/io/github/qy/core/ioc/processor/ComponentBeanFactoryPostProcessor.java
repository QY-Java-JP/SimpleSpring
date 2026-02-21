package io.github.qy.core.ioc.processor;

import io.github.qy.annotation.ioc.Component;
import io.github.qy.bean.ioc.BeanDefinition;
import io.github.qy.core.ioc.BeanDefinitionPostProcessor;
import io.github.qy.exception.bean.BeanDefinitionCreateException;
import io.github.qy.util.BeanUtil;
import io.github.qy.util.UrlUtil;

import java.util.List;
import java.util.Map;

public class ComponentBeanFactoryPostProcessor implements BeanDefinitionPostProcessor {

    public static final String BEAN_NAME = "componentBeanFactoryPostProcessor";

    // 启动类
    private final Class<?> runClass;

    public ComponentBeanFactoryPostProcessor(Class<?> runClass) {
        this.runClass = runClass;
    }

    @Override
    public void postProcessorBeanDefinition(Map<String, BeanDefinition> definitionMap) throws BeanDefinitionCreateException {
        // 首先拿到所有的class路径 只要是有@Component的都加进去
        final List<String> classUrls = UrlUtil.scanClasses(runClass);
        for (String url : classUrls) {
            Class<?> urlClass;
            try {
                urlClass = Class.forName(url);
            } catch (ClassNotFoundException e) {
                throw new BeanDefinitionCreateException(e.getMessage());
            }

            final Component component = BeanUtil.findAnnotation(urlClass, Component.class);
            if (component == null) continue;

            // 解析 加入
            final BeanDefinition bd = BeanUtil.createBeanDefinition(urlClass, component.value());
            if (!bd.isIndependent()) continue;

            definitionMap.put(bd.getBeanName(), bd);
        }
    }

}
