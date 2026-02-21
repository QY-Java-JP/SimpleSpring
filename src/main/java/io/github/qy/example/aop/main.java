package io.github.qy.example.aop;

import io.github.qy.core.ioc.SimpleSpringHelper;

public class main {
    public static void main(String[] args) {
        SimpleSpringHelper.initStaticApplicationContext(main.class);
        UserService u = (UserService) SimpleSpringHelper.getStaticContext().getSingleBean(UserService.class);
        u.t1();
    }
}
