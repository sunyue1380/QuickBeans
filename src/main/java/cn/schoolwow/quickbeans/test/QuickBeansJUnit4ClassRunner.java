package cn.schoolwow.quickbeans.test;

import cn.schoolwow.quickbeans.QuickBeans;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class QuickBeansJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    public QuickBeansJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    protected Object createTest() throws Exception {
        QuickBeans quickBeans = new QuickBeans();
        Class c = this.getTestClass().getJavaClass();
        quickBeans.register(c);
        quickBeans.refresh();
        return quickBeans.getBean(c.getName());
    }
}
