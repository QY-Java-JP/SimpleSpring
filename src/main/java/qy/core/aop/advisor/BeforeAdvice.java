package qy.core.aop.advisor;

import qy.core.aop.JoinPoint;
import qy.core.aop.ReflectiveMethodInvocation;

public abstract class BeforeAdvice extends AbsMethodInvocationAdvice{

    // 前置逻辑
    protected abstract void invoke(JoinPoint point) throws Exception;

    @Override
    public Object invokeAdvice(JoinPoint point) throws Throwable {
        invoke(point);
        return proceed();
    }
}
