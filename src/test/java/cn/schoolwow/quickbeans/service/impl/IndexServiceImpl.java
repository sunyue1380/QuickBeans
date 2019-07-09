package cn.schoolwow.quickbeans.service.impl;

import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.annotation.Scheduled;
import cn.schoolwow.quickbeans.service.IndexService;

@Component
public class IndexServiceImpl implements IndexService {
    public boolean register(String username,String password){
        if("quickserver".equalsIgnoreCase(username)&&
                "123456".equalsIgnoreCase(password)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    @Scheduled(fixedDelay = 10000)
    public void schedule() {
        System.out.println("这段语句每隔10秒钟输出一次!");
    }
}
