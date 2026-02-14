package qy.core.aop.advisor;

import qy.core.aop.ReflectiveMethodInvocation;
import qy.core.aop.pointCut.PointCut;

public abstract class AbsMethodInvocationAdvice implements Advice {

    // 调用链
    private ReflectiveMethodInvocation methodInvocation;

    // 调用下一个调用链
    protected Object proceed() throws Throwable {
        return methodInvocation.proceed();
    }

    @Override
    public void setInvocation(ReflectiveMethodInvocation invocation) {
        this.methodInvocation = invocation;
    }

    // 获取这个切片的切点
    public abstract PointCut getPointCut();
}
