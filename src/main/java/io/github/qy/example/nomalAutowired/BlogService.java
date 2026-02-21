package io.github.qy.example.nomalAutowired;

import io.github.qy.annotation.ioc.Autowired;
import io.github.qy.annotation.ioc.Component;
import io.github.qy.example.aop.T1;

@Component
public class BlogService {

    @Autowired
    private UserService userService;

    @T1
    public void t1(){
        System.out.println(userService);
    }
}
