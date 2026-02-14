package qy.core.ioc.processor;

import lombok.extern.slf4j.Slf4j;
import qy.annotation.ioc.Autowired;
import qy.core.ioc.factory.BeanFactory;
import qy.core.ioc.factory.aware.BeanFactoryAware;
import qy.core.ioc.factory.aware.InstantiationAwareBeanPostProcessor;
import qy.exception.bean.BeanCreateException;
import qy.util.BeanUtil;
import qy.util.StringUtil;

import java.lang.reflect.Field;
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
            // 先根据type 如果拿不到再根据name
            Class<?> fileType = field.getType();
            List<Object> fileByTypeList = beanFactory.getBean(fileType);
            // 如果有多个也用name
            if (fileByTypeList.size() == 1) {
                propValueMap.put(field.getName(), fileByTypeList.get(0));
                continue;
            }

            // 用Name
            String standName = field.getAnnotation(Autowired.class).value();
            if (!StringUtil.hasText(standName)) {
                throw new BeanCreateException("在给bean:" + beanName + "依赖注入时 类型:" + fileType.getSimpleName()
                        + "有多个或零个值又未在@Autowired注解内写name");
            }

            Object fileByName = beanFactory.getBean(standName);
            if (fileByName == null) {
                throw new BeanCreateException("在给bean:" + beanName + "依赖注入时 类型:" + fileType.getSimpleName()
                        + "未找到合适的类型且名字为" + standName + "的类不存在");
            }

            propValueMap.put(field.getName(), fileByName);
        }

    }

    @Override
    public void setBeanFactory(BeanFactory factory) {
        beanFactory = factory;
    }
}
