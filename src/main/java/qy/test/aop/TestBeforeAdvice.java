package qy.test.aop;

import qy.annotation.ioc.Component;
import qy.core.aop.JoinPoint;
import qy.core.aop.ReflectiveMethodInvocation;
import qy.core.aop.advisor.BeforeAdvice;
import qy.core.aop.pointCut.PointCut;
import qy.core.aop.pointCut.PointCutHelper;

@Component
public class TestBeforeAdvice extends BeforeAdvice {
    @Override
    protected void invoke(JoinPoint point) throws Exception {
        System.out.println(1);
    }

    @Override
    public PointCut getPointCut() {
        return PointCutHelper.createAnnotationPointCut(T1.class);
    }
}
