package qy.util;

import org.apache.logging.log4j.core.config.Order;
import org.jetbrains.annotations.Nullable;
import qy.annotation.ioc.PostConstruct;
import qy.bean.OrderEntry;
import qy.bean.ioc.BeanDefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BeanUtil {

    //====================================注解================================================
    // 拿出某个注解
    @Nullable
    public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> type){
        return expendClassAnnotation(clazz).stream()
                .filter(a -> a.annotationType() == type)
                .map(type::cast)
                .findFirst()
                .orElse(null);
    }

    // 是否有某个注解
    public static boolean hasThisAnnotation(Class<?> clazz, Class<? extends Annotation> type){
        return findAnnotation(clazz, type) != null;
    }

    // 把注解平铺开
    public static List<Annotation> expendClassAnnotation(Class<?> clazz) {
        final List<Annotation> annotations = new ArrayList<>();
        expendClassAnnotation0(clazz, annotations);

        return annotations;
    }
    private static void expendClassAnnotation0(Class<?> clazz, List<Annotation> list){
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            // 排除元注解
            if (annotation.annotationType().getPackageName().startsWith("java.lang.annotation")) continue;

            list.add(annotation);
            expendClassAnnotation0(annotation.annotationType(), list);
        }
    }

    // 查找含有某个注解的方法
    public static List<Method> findHasThisAnnotationMethods(Class<?> clazz, Class<? extends Annotation> annotation){
        final Method[] methods = clazz.getMethods();
        final List<Method> retList = new ArrayList<>();
        for (Method method : methods) {
            if (method.getAnnotation(annotation) != null) {
                retList.add(method);
            }
        }

        return retList;
    }

    // 查找含有某个注解的属性
    public static List<Field> findHasThisAnnotationField(Class<?> clazz, Class<? extends Annotation> annotation){
        final Field[] fields = clazz.getDeclaredFields();
        final List<Field> retList = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(annotation) != null) {
                retList.add(field);
            }
        }

        return retList;
    }

    // 根据Order注解排序
    public static <T> void sortByOrder(List<T> dataList){
        // 我们整理成一个OrderEntry 然后排序之后再放进去
        final List<OrderEntry<T>> orderList = new ArrayList<>(dataList.size());
        for (T o : dataList) {
            Order order = findAnnotation(o.getClass(), Order.class);
            orderList.add(new OrderEntry<T>(order == null ? 0 : order.value(), o));
        }

        orderList.sort(Comparator.comparingInt(OrderEntry::getOrder));
        dataList.clear();
        orderList.forEach(o -> dataList.add(o.getData()));
    }

    //=================================bean=======================================================
    // 获取bean默认名称
    public static String getDefaultBeanName(Class<?> clazz){
        final String name = clazz.getSimpleName();
        if (name.length() > 1 && Character.isUpperCase(name.charAt(0)) && Character.isUpperCase(name.charAt(1))){
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    // 规范bean名称
    public static String standBeanName(Class<?> clazz, @Nullable String name){
        return StringUtil.hasText(name) ? name : getDefaultBeanName(clazz);
    }

    // 根据一个class生成bean描述信息
    public static BeanDefinition createBeanDefinition(Class<?> clazz, @Nullable String beanName){
        // 首先填入基础信息
        final BeanDefinition definition = new BeanDefinition(standBeanName(clazz, beanName), clazz);

        // 初始化方法
        final List<Method> initMethod = findHasThisAnnotationMethods(clazz, PostConstruct.class);
        if (!initMethod.isEmpty()) {
            definition.setInitMethodName(initMethod.get(0).getName());
        }

        return definition;
    }
    public static BeanDefinition createBeanDefinition(Class<?> clazz){
        return createBeanDefinition(clazz, null);
    }

}
