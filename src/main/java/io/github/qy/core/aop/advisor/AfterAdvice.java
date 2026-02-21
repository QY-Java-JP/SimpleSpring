package io.github.qy.core.aop.advisor;

import io.github.qy.core.aop.JoinPoint;

public abstract class AfterAdvice extends AbsMethodInvocationAdvice{

    // 后置逻辑
    protected abstract void invoke(JoinPoint point) throws Exception;

    @Override
    public Object invokeAdvice(JoinPoint point) throws Throwable {
        Object proceed = proceed();
        invoke(point);
        return proceed;
    }
}
