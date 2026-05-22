package io.github.qy.example.nomalAutowired;

import io.github.qy.annotation.ioc.Component;
import io.github.qy.annotation.ioc.PostConstruct;

@Component
public class UserService {

    private BlogService blogService;

    public UserService(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostConstruct
    public void init(){
        System.out.println("UserService init");
    }

    public void t1(){
        System.out.println(blogService);
    }
}
