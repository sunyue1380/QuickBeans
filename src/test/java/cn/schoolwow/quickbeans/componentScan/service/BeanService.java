package cn.schoolwow.quickbeans.componentScan.service;

import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.annotation.Scheduled;

@Component
public class BeanService {
    @Scheduled(initialDelay = 0,fixedDelay = 1000)
    public void scheduleJob(){
        System.out.println("定时任务执行了!");
    }
}
