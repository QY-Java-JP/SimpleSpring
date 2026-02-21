package io.github.qy.example.listAutowired;

import io.github.qy.annotation.ioc.Autowired;
import io.github.qy.annotation.ioc.Component;
import io.github.qy.annotation.ioc.PostConstruct;

import java.util.List;

@Component
public class UserService {

    @Autowired
    private List<Object> objects;

    @PostConstruct
    public void init(){
        System.out.println("UserService init");
    }

    public void t1(){
        objects.forEach(System.out::println);
    }
}
