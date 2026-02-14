package qy.core.aop.advisor;

import qy.core.aop.JoinPoint;

public abstract class AroundAdvice extends AbsMethodInvocationAdvice {
    // 环绕逻辑
    protected abstract Object invoke(JoinPoint point) throws Exception;

    @Override
    public Object invokeAdvice(JoinPoint point) throws Exception {
        return invoke(point);
    }
}
