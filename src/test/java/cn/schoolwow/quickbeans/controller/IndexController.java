package cn.schoolwow.quickbeans.controller;

import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.service.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Resource
    private IndexService indexService;

    public boolean register(
            String username,
            String password
    ){
        logger.info("[注册用户]用户名:{},密码:{}",username,password);
        return indexService.register(username,password);
    }

    @PostConstruct
    public void init(){
        logger.info("[初始化]执行IndexController的初始化方法!");
    }
}
