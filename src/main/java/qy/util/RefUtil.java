package qy.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

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
}
