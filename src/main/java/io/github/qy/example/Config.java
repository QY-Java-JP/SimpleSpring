package io.github.qy.example;

import io.github.qy.annotation.ioc.Bean;
import io.github.qy.annotation.ioc.Configurable;
import io.github.qy.example.nomalAutowired.UserService;

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
