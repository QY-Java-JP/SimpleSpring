package qy.example.mapAutowired;

import qy.annotation.ioc.Autowired;
import qy.annotation.ioc.Component;
import qy.annotation.ioc.PostConstruct;

import java.util.List;
import java.util.Map;

@Component
public class UserService {

    @Autowired
    private Map<String, Object> objects;

    @PostConstruct
    public void init(){
        System.out.println("UserService init");
    }

    public void t1(){
        objects.forEach((key, value) -> {
            System.out.print("key: " + key);
            System.out.println("value: " + value);
        });
    }
}
