package cn.schoolwow.quickbeans.signer;

import cn.schoolwow.quickbeans.QuickBeans;
import org.junit.Test;

import java.util.List;

public class SignerTest {

    @Test
    public void testHandler(){
        QuickBeans quickBeans = new QuickBeans();
        quickBeans.scan("cn.schoolwow.quickbeans.signer");
        quickBeans.refresh();
        List<Signer> handlers = quickBeans.getBeanList(Signer.class);
        System.out.println(handlers);
    }
}
