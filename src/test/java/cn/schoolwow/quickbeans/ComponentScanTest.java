package cn.schoolwow.quickbeans;

import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.annotation.ComponentScan;
import cn.schoolwow.quickbeans.config.BeanConfig;
import cn.schoolwow.quickbeans.entity.User;
import cn.schoolwow.quickbeans.test.QuickBeansJUnit4ClassRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;

@RunWith(QuickBeansJUnit4ClassRunner.class)
@ComponentScan(basePackageClasses = {BeanConfig.class})
@Component
public class ComponentScanTest {
    private Logger logger = LoggerFactory.getLogger(ComponentScanTest.class);
    @Resource
    private User user;

    @Test
    public void testComponentScan() throws ClassNotFoundException, IOException {
        logger.info("user:{}",user);
        Assert.assertEquals(true,user!=null);
    }
}
