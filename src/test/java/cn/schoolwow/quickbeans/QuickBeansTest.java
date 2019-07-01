package cn.schoolwow.quickbeans;

import cn.schoolwow.quickbeans.config.BeanConfig;
import cn.schoolwow.quickbeans.entity.Talk;
import cn.schoolwow.quickbeans.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class QuickBeansTest {
    private Logger logger = LoggerFactory.getLogger(QuickBeansTest.class);
    @Test
    public void testScan() throws Exception{
        QuickBeans quickBeans = new QuickBeans();
        quickBeans.register(BeanConfig.class);
        quickBeans.refresh();

        String[] expectNames = {
                "cn.schoolwow.quickbeans.controller.IndexController",
                "cn.schoolwow.quickbeans.service.IndexService",
                "cn.schoolwow.quickbeans.service.IndexServiceImpl",
                "cn.schoolwow.quickbeans.config.BeanConfig",
        };
        Set<String> beanNameSet = quickBeans.getBeanNameSet();
        for(String expectName:expectNames){
            Assert.assertEquals(true,beanNameSet.contains(expectName));
        }
    }

    @Test
    public void testPrototype() throws Exception{
        QuickBeans quickBeans = new QuickBeans();
        quickBeans.register(User.class);
        quickBeans.refresh();

        User user1 = quickBeans.getBean(User.class,"user");
        User user2 = quickBeans.getBean(User.class,"user");
        Assert.assertEquals(true,user1!=user2);
    }

    @Test
    public void testPrototype2() throws Exception{
        QuickBeans quickBeans = new QuickBeans();
        quickBeans.scan("cn.schoolwow.quickbeans.entity");
        quickBeans.refresh();

        Talk talk1 = quickBeans.getBean(Talk.class,"talk");
        User user1 = talk1.getUser();
        Talk talk2 = quickBeans.getBean(Talk.class,"talk");
        User user2 = talk2.getUser();
        Assert.assertEquals(true,user1!=user2);
    }
}
