package qy.core.aop.pointCut;

import java.lang.reflect.Method;

public interface PointCut {
    // 是否切入
    boolean needCut(Method method);
}
