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
            // 支持List Map 普通
            Object value;
            Class<?> fieldType = field.getType();
            if (List.class.isAssignableFrom(fieldType)) {
                value = findThisTypeBeans(field);
            } else if (Map.class.isAssignableFrom(fieldType)) {
                value = findThisTypeBeansAndName(field);
            } else {
                value = findThisTypeOrNameBean(field, beanName);
            }

            propValueMap.put(field.getName(), value);
        }
    }

    // 注入List
    private List<Object> findThisTypeBeans(Field list) throws BeanCreateException {
        // 我们要拿到里面的泛型
        Class<?> type;
        try {
            type = RefUtil.getNoNestingListGenericType(list);
        } catch (HasNestingException e) {
            throw new BeanCreateException("构建" + list.getName() + "仅支持单层List<> 请勿使用嵌套泛型");
        }

        return beanFactory.getBean(type);
    }

    // 注入map
    private Map<String, Object> findThisTypeBeansAndName(Field map) throws BeanCreateException {
        Class<?>[] types;
        try {
            types = RefUtil.getNoNestingMapGenericType(map);
        } catch (HasNestingException e) {
            throw new BeanCreateException("构建" + map.getName() + "仅支持单层Map<> 请勿使用嵌套泛型");
        }
        // 还要检查key是否是String
        if (!String.class.isAssignableFrom(types[0])) {
            throw new BeanCreateException("Map的key只支持String类型");
        }

        final List<String> names = beanFactory.getBeanNames(types[1]);
        final Map<String, Object> retMap = new HashMap<>();
        names.forEach(n -> retMap.put(n, beanFactory.getBean(n)));

        return retMap;
    }

    // 注入普通属性
    private Object findThisTypeOrNameBean(Field field, String beanName){
        final Class<?> fileType = field.getType();
        final List<Object> fileByTypeList = beanFactory.getBean(fileType);
        // 如果有多个也用name
        if (fileByTypeList.size() == 1) {
            return fileByTypeList.get(0);
        }

        // 用Name
        final String standName = field.getAnnotation(Autowired.class).value();
        if (!StringUtil.hasText(standName)) {
            throw new BeanCreateException("在给bean:" + beanName + "依赖注入时 类型:" + fileType
                    + "有多个或零个值又未在@Autowired注解内写name");
        }

        final Object fileByName = beanFactory.getBean(standName);
        if (fileByName == null) {
            throw new BeanCreateException("在给bean:" + beanName + "依赖注入时 类型:" + fileType
                    + "未找到合适的类型且名字为" + standName + "的类不存在");
        }

        return fileByName;
    }

    @Override
    public void setBeanFactory(BeanFactory factory) {
        beanFactory = factory;
    }
}
