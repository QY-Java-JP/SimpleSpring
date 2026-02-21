package io.github.qy.core.aop.advisor;

import io.github.qy.core.aop.JoinPoint;
import io.github.qy.core.aop.ReflectiveMethodInvocation;

public interface Advice {
    // 执行切片
    Object invokeAdvice(JoinPoint point) throws Throwable;

    // 设置执行链
    void setInvocation(ReflectiveMethodInvocation invocation);
}
