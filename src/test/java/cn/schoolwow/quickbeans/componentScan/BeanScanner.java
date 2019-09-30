package cn.schoolwow.quickbeans.componentScan;

import cn.schoolwow.quickbeans.annotation.Bean;
import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.annotation.ComponentScan;
import cn.schoolwow.quickbeans.componentScan.service.BeanService;

import javax.annotation.Resource;

@ComponentScan(basePackages = {"cn.schoolwow.quickbeans.componentScan.service"})
@Component
public class BeanScanner {
    @Resource
    private BeanService beanService;
    @Bean(name = "myBeanService")
    public BeanService beanService(){
        return new BeanService();
    }
}
