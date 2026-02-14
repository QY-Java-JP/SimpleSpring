package qy.core.aop.advisor;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import qy.core.aop.ReflectiveMethodInvocation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class AopInterceptor implements MethodInterceptor {

    // 这个类所有方法的切面
    private final Map<Method, List<Advice>> methodAdviceMap;
    // 原始对象 (代理前在走bean生命周期的对象)
    private final Object target;

    public AopInterceptor(Object target, Map<Method, List<Advice>> methodAdviceMap) {
        this.methodAdviceMap = methodAdviceMap;
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // 拿到这个方法的切片
        final List<Advice> adviceList = methodAdviceMap.get(method);
        if (adviceList == null) return methodProxy.invokeSuper(o, args);

        // 生成执行器 执行
        final ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(
                adviceList, target, o, methodProxy, args);
        adviceList.forEach(a -> a.setInvocation(invocation));

        return invocation.proceed();
    }
}
