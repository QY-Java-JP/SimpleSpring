package qy;

import org.junit.jupiter.api.Test;
import qy.annotation.ioc.Configurable;
import qy.util.BeanUtil;

import java.lang.annotation.Annotation;
import java.util.List;

public class AppTest {

    @Test
    public void t1(){
        List<Annotation> annotations = BeanUtil.expendClassAnnotation(Configurable.class);
        System.out.println(annotations.size());
    }

}
