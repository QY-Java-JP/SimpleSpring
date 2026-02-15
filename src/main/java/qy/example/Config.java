package qy.example;

import qy.annotation.ioc.Bean;
import qy.annotation.ioc.Configurable;
import qy.example.nomalAutowired.UserService;

import java.util.Random;

@Configurable
public class Config {

    @Bean
    public Random random(){
        return new Random();
    }

    @Bean
    public UserService userService(){
        return new UserService();
    }

}
