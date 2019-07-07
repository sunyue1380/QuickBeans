package cn.schoolwow.quickbeans;

import cn.schoolwow.quickbeans.config.BeanConfig;
import cn.schoolwow.quickbeans.entity.Talk;
import cn.schoolwow.quickbeans.entity.User;
import cn.schoolwow.quickbeans.signer.SignerHolder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
                "cn.schoolwow.quickbeans.service.impl.IndexServiceImpl",
                "cn.schoolwow.quickbeans.config.BeanConfig",
        };
        List<String> beanNameSet = quickBeans.getBeanNameList();
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

    @Test
    public void testGetBeanList() throws Exception{
        QuickBeans quickBeans = new QuickBeans();
        quickBeans.scan("cn.schoolwow.quickbeans.signer");
        quickBeans.refresh();

        List<Object> signerList = quickBeans.getBeanList("signer");
        Assert.assertEquals(2,signerList.size());
        SignerHolder signerHolder = quickBeans.getBean(SignerHolder.class);
        Assert.assertEquals(true,signerHolder.signerList!=null);
    }
}