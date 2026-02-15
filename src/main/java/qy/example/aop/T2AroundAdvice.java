package qy.example.aop;

import qy.annotation.ioc.Component;
import qy.core.aop.JoinPoint;
import qy.core.aop.advisor.AroundAdvice;
import qy.core.aop.pointCut.PointCut;
import qy.core.aop.pointCut.PointCutHelper;

@Component
public class T2AroundAdvice extends AroundAdvice {
    @Override
    protected Object invoke(JoinPoint point) throws Throwable {
        System.out.println("T2Around start...");
        Object o = proceed();
        System.out.println("T2Around end...");
        return o;
    }

    @Override
    public PointCut getPointCut() {
        return PointCutHelper.createAnnotationPointCut(T2.class);
    }
}
