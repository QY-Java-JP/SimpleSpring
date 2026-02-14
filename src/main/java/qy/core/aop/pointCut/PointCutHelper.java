package qy.core.aop.pointCut;

import java.lang.annotation.Annotation;

public class PointCutHelper {

    // 生成一个注解的切点
    public static PointCut createAnnotationPointCut(Class<? extends Annotation> clazz){
        return new AnnotationPointCut(clazz);
    }

}
