package cn.schoolwow.quickbeans;

import cn.schoolwow.quickbeans.config.BeanConfig;
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
        Set<String> beanNameSet = quickBeans.getBeanNameSet();
        for(String beanName:beanNameSet){
            logger.info("[bean]name:{},instance:{}",beanName,quickBeans.getBean(beanName));
        }
        //取两次User,它们应该不是同一个对象
        User user1 = quickBeans.getBean(User.class,"user");
        User user2 = quickBeans.getBean(User.class,"user");
        Assert.assertTrue(user1!=user2);
    }
}
