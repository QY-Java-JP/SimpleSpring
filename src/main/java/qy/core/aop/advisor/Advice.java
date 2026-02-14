package qy.core.aop.advisor;

import qy.core.aop.JoinPoint;
import qy.core.aop.ReflectiveMethodInvocation;

public interface Advice {
    // 执行切片
    Object invokeAdvice(JoinPoint point) throws Throwable;

    // 设置执行链
    void setInvocation(ReflectiveMethodInvocation invocation);
}
