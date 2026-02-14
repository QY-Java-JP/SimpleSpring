package qy.test;

import qy.annotation.ioc.Bean;
import qy.annotation.ioc.Configurable;

import java.util.Random;

//@Configurable
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
