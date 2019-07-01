package cn.schoolwow.quickbeans.config;

import cn.schoolwow.quickbeans.annotation.Bean;
import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.annotation.ComponentScan;
import cn.schoolwow.quickbeans.entity.User;

import javax.annotation.Resource;

@ComponentScan(basePackages = {"cn.schoolwow.quickbeans.service","cn.schoolwow.quickbeans.controller"})
@Component
public class BeanConfig {
    @Resource
    private User user;

    @Bean(name = "myUser")
    public User user(){
        return new User();
    }
}
