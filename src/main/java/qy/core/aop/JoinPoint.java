package qy.core.aop;

import net.sf.cglib.proxy.MethodProxy;

public class JoinPoint {

    // 被代理前的对象
    private final Object targetBean;
    // 当前方法
    private final MethodProxy method;

    public JoinPoint(Object targetBean, MethodProxy method) {
        this.targetBean = targetBean;
        this.method = method;
    }

    // 获取代理前对象本体
    public Object getTarget(){
        return targetBean;
    }

    // 获取方法签名
    public MethodProxy getMethod(){
        return method;
    }
}
