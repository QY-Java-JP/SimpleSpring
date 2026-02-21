package io.github.qy.core.aop.advisor;

import lombok.Getter;
import io.github.qy.core.aop.pointCut.PointCut;

@Getter
public class Aspect {
    // 切点
    private final PointCut pointCut;
    // 切片
    private final Advice advice;

    public Aspect(PointCut pointCut, Advice advice) {
        this.pointCut = pointCut;
        this.advice = advice;
    }

}
