package cn.schoolwow.quickbeans.component;

import cn.schoolwow.quickbeans.annotation.Component;

import javax.annotation.PostConstruct;

@Component
public class MyComponent1{
    public boolean hasInitialize = false;

    @PostConstruct
    public void postConstruct(){
        hasInitialize = true;
    }
}