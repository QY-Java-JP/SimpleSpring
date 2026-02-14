package qy.test;

import qy.annotation.ioc.Autowired;
import qy.annotation.ioc.Component;
import qy.test.aop.T1;

@Component
public class BlogService {

    @Autowired
    private UserService userService;

    @T1
    public void t1(){
        System.out.println(userService);
    }
}
