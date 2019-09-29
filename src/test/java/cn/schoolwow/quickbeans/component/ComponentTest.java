package cn.schoolwow.quickbeans.component;

import cn.schoolwow.quickbeans.QuickBeans;
import org.junit.Assert;
import org.junit.Test;

public class ComponentTest {

    @Test
    public void testComponent(){
        QuickBeans quickBeans = new QuickBeans();
        quickBeans.register(MyComponent1.class);
        quickBeans.register(MyComponent2.class);
        quickBeans.refresh();
        MyComponent1 myComponent1 = quickBeans.getBean(MyComponent1.class);
        Assert.assertEquals(true,myComponent1.hasInitialize);

        MyComponent2 myComponent2 = quickBeans.getBean(MyComponent2.class);
        Assert.assertEquals(true,myComponent2.hasInitialize);

        Assert.assertNotNull("注入依赖失败!",myComponent2.myComponent1);
    }
}
