package qy.util;

import org.jetbrains.annotations.Nullable;
import qy.exception.HasNestingException;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RefUtil {
    // 给一个属性赋值
    public static boolean setProp(Object o, String name, Object value){
        final Class<?> clazz = o.getClass();
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(o, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }

        return true;
    }

    // 拿到一个属性的值
    @Nullable
    public static Object getProp(Object o, String name){
        final Class<?> clazz = o.getClass();
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    // 拿取一个构造方法
    @Nullable
    public static Constructor<?> findConstructor(Class<?> clazz, Class<?>... args){
        try {
            return clazz.getConstructor(args);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    // 执行某个方法
    public static boolean runMethod(Object o, String methodName, Object... args){
        final Class<?> clazz = o.getClass();
        final Class<?>[] classTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);

        try {
            Method method = clazz.getMethod(methodName, classTypes);
            method.invoke(o, args);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return false;
        }

        return true;
    }

    // 获取List的泛型
    public static Class<?> getNoNestingListGenericType(Field field) throws HasNestingException {
        // 只拿一层即可 其他情况异常
        final Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType pt)) {
            throw new RuntimeException(field.getName() + "不是一个List");
        }
        if (!List.class.isAssignableFrom((Class<?>) pt.getRawType())) {
            throw new RuntimeException(field.getName() + "不是一个List");
        }

        final Type listType = pt.getActualTypeArguments()[0];
        if (!(listType instanceof Class<?> typeClass)) {
            throw new HasNestingException();
        }

        return typeClass;
    }

    // 获取map的泛型
    public static Class<?>[] getNoNestingMapGenericType(Field field) throws HasNestingException{
        final Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType pt)) {
            throw new RuntimeException(field.getName() + "不是一个Map");
        }
        if (!Map.class.isAssignableFrom((Class<?>) pt.getRawType())) {
            throw new RuntimeException(field.getName() + "不是一个Map");
        }

        final Type[] types = pt.getActualTypeArguments();
        final Class<?>[] retArray = new Class[2];
        for (int i = 0; i < 2; i++) {
            if (!(types[i] instanceof Class<?> typeClass)) {
                throw new HasNestingException();
            }
            retArray[i] = typeClass;
        }

        return retArray;
    }

}
