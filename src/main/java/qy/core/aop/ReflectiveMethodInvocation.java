package qy.core.aop;

import net.sf.cglib.proxy.MethodProxy;
import qy.core.aop.advisor.Advice;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation {

    // 这个方法的代理链
    private final List<Advice> adviceList;
    // 当前执行到的切片
    private int currentInterceptorIndex = -1;

    // 代理前的类
    private final Object targetObj;
    // 代理后的类
    private final Object proxyObj;
    // 被代理的源方法
    private final MethodProxy method;
    // 被代理的方法的参数
    private final Object[] args;
    // 切点封装类
    private final JoinPoint joinPoint;

    public ReflectiveMethodInvocation(List<Advice> adviceList, Object targetObj, Object proxyObj,
                                      MethodProxy method, Object[] args) {
        this.adviceList = adviceList;
        this.targetObj = targetObj;
        this.proxyObj = proxyObj;
        this.method = method;
        this.args = args;

        joinPoint = new JoinPoint(targetObj, method);
    }

    // 调用下一个代理链
    public Object proceed() throws Throwable {
        // 看看是不是该运行原方法了
        if (currentInterceptorIndex == adviceList.size() - 1) {
            return method.invoke(targetObj, args);
        }

        // 调用下一个执行链上的逻辑
        final Advice nextAdvice = adviceList.get(++currentInterceptorIndex);
        return nextAdvice.invokeAdvice(joinPoint);
    }

}
