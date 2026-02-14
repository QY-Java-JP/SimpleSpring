package qy.test;

import qy.annotation.ioc.Autowired;
import qy.annotation.ioc.Component;
import qy.annotation.ioc.PostConstruct;

@Component
public class UserService {

    @Autowired
    private BlogService blogService;

    @PostConstruct
    public void init(){
        System.out.println("UserService init");
    }

}
