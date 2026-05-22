package io.github.qy.core.ioc.processor;

import lombok.extern.slf4j.Slf4j;
import io.github.qy.annotation.ioc.Autowired;
import io.github.qy.core.ioc.factory.BeanFactory;
import io.github.qy.core.ioc.factory.aware.BeanFactoryAware;
import io.github.qy.core.ioc.factory.aware.InstantiationAwareBeanPostProcessor;
import io.github.qy.exception.HasNestingException;
import io.github.qy.exception.bean.BeanCreateException;
import io.github.qy.util.BeanUtil;
import io.github.qy.util.RefUtil;
import io.github.qy.util.StringUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AutowiredBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    public static final String BEAN_NAME = "autowiredBeanPostProcessor";

    // bean工厂
    private BeanFactory beanFactory;

    @Override
    public void postProcessorProcessor(Map<String, Object> propValueMap, Object bean, String beanName)
            throws BeanCreateException {
        // 拿到所有有@Autowired注解的属性 然后一个个getBean()
        final Class<?> beanClass = bean.getClass();
        final List<Field> fields = BeanUtil.findHasThisAnnotationField(beanClass, Autowired.class);
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            propValueMap.put(field.getName(), beanFactory.resolveDependency(field.getGenericType(),
                    beanName, autowired.value()));
        }
    }



    @Override
    public void setBeanFactory(BeanFactory factory) {
        beanFactory = factory;
    }
}
