package cn.schoolwow.quickbeans.component;

import cn.schoolwow.quickbeans.annotation.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class MyComponent2 {
    @Resource
    public MyComponent1 myComponent1;

    public boolean hasInitialize = false;

    @PostConstruct
    public void postConstruct(){
        hasInitialize = true;
    }
}