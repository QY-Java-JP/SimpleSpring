package io.github.qy.example.aop;

import io.github.qy.annotation.ioc.Component;
import io.github.qy.core.aop.JoinPoint;
import io.github.qy.core.aop.advisor.AroundAdvice;
import io.github.qy.core.aop.pointCut.PointCut;
import io.github.qy.core.aop.pointCut.PointCutHelper;

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
