package cn.schoolwow.quickbeans.config;

import cn.schoolwow.quickbeans.annotation.Bean;
import cn.schoolwow.quickbeans.annotation.ComponentScan;
import cn.schoolwow.quickbeans.entity.User;

@ComponentScan(basePackages = "cn.schoolwow.quickbeans.entity")
public class BeanConfig {
    @Bean
    public User myUser(){
        return new User();
    }
}
