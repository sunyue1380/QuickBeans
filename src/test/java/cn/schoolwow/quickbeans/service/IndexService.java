package cn.schoolwow.quickbeans.service;

public interface IndexService {
    /**注册*/
    boolean register(String username,String password);

    /**定时任务*/
    void schedule();
}
