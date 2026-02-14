package qy.core.aop.advisor;

import qy.core.aop.JoinPoint;
import qy.core.aop.ReflectiveMethodInvocation;

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
