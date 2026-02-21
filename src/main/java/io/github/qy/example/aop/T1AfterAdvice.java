package io.github.qy.example.aop;

import io.github.qy.annotation.ioc.Component;
import io.github.qy.core.aop.JoinPoint;
import io.github.qy.core.aop.advisor.AfterAdvice;
import io.github.qy.core.aop.pointCut.PointCut;
import io.github.qy.core.aop.pointCut.PointCutHelper;

@Component
public class T1AfterAdvice extends AfterAdvice {
    @Override
    protected void invoke(JoinPoint point) throws Exception {
        System.out.println("T1After");
    }

    @Override
    public PointCut getPointCut() {
        return PointCutHelper.createAnnotationPointCut(T1.class);
    }
}
