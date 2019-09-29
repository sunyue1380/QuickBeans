package cn.schoolwow.quickbeans.componentScan;

import cn.schoolwow.quickbeans.QuickBeans;
import cn.schoolwow.quickbeans.componentScan.service.BeanService;
import org.junit.Assert;
import org.junit.Test;

public class ComponentScanTest {

    @Test
    public void testComponent(){
        QuickBeans quickBeans = new QuickBeans();
        quickBeans.register(BeanScanner.class);
        quickBeans.refresh();

        BeanService beanService1 = quickBeans.getBean(BeanService.class);
        BeanService beanService2 = quickBeans.getBean("myBeanService",BeanService.class);
        Assert.assertEquals(true,beanService1==beanService2);
    }
}
