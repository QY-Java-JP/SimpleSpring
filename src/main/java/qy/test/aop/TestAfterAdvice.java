package qy.test.aop;

import qy.annotation.ioc.Component;
import qy.core.aop.JoinPoint;
import qy.core.aop.advisor.AfterAdvice;
import qy.core.aop.pointCut.PointCut;
import qy.core.aop.pointCut.PointCutHelper;

@Component
public class TestAfterAdvice extends AfterAdvice {
    @Override
    protected void invoke(JoinPoint point) throws Exception {
        System.out.println(2);
        System.out.println(point.getTarget().getClass());
    }

    @Override
    public PointCut getPointCut() {
        return PointCutHelper.createAnnotationPointCut(T1.class);
    }
}
