package io.github.qy.example.aop;

import io.github.qy.annotation.ioc.Component;
import io.github.qy.core.aop.JoinPoint;
import io.github.qy.core.aop.advisor.BeforeAdvice;
import io.github.qy.core.aop.pointCut.PointCut;
import io.github.qy.core.aop.pointCut.PointCutHelper;

@Component
public class T1BeforeAdvice extends BeforeAdvice {
    @Override
    protected void invoke(JoinPoint point) throws Exception {
        System.out.println("T1Before");
    }

    @Override
    public PointCut getPointCut() {
        return PointCutHelper.createAnnotationPointCut(T1.class);
    }
}
