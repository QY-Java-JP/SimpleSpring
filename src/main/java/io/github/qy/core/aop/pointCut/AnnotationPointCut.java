package io.github.qy.core.aop.pointCut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationPointCut implements PointCut{

    // 注解
    private final Class<? extends Annotation> annotationClass;

    public AnnotationPointCut(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public boolean needCut(Method method) {
        // 方法上是否有这个注解
        return method.getAnnotation(annotationClass) != null;
    }
}
