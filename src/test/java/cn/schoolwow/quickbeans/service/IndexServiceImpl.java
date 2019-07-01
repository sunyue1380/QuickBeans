package cn.schoolwow.quickbeans.service;

import cn.schoolwow.quickbeans.annotation.Component;

@Component
public class IndexServiceImpl implements IndexService{
    public boolean register(String username,String password){
        if("quickserver".equalsIgnoreCase(username)&&
                "123456".equalsIgnoreCase(password)){
            return true;
        }else{
            return false;
        }
    }
}
