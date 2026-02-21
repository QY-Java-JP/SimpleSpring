package io.github.qy.example.nomalAutowired;

import io.github.qy.annotation.ioc.Autowired;
import io.github.qy.annotation.ioc.Component;
import io.github.qy.annotation.ioc.PostConstruct;

@Component
public class UserService {

    @Autowired
    private BlogService blogService;

    @PostConstruct
    public void init(){
        System.out.println("UserService init");
    }

    public void t1(){
        System.out.println(blogService);
    }
}
