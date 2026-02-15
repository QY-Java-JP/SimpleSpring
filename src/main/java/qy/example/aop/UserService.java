package qy.example.aop;

import qy.annotation.ioc.Component;

@Component
public class UserService {

    @T1
    @T2
    public void t1(){
        System.out.println("t1");
    }

}
