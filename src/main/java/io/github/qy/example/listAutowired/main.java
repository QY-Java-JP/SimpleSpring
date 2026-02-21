package io.github.qy.example.listAutowired;

import io.github.qy.core.ioc.SimpleSpringHelper;

public class main {
    public static void main(String[] args) {
        SimpleSpringHelper.initStaticApplicationContext(main.class);
        UserService userService = (UserService) SimpleSpringHelper.getStaticContext().getSingleBean(UserService.class);
        userService.t1();
    }
}
