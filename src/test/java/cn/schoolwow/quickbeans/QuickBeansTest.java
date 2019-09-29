package cn.schoolwow.quickbeans;

import cn.schoolwow.quickbeans.annotation.ComponentScan;
import cn.schoolwow.quickbeans.signer.Signer;
import cn.schoolwow.quickbeans.test.QuickBeansJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import java.util.List;

@RunWith(QuickBeansJUnit4ClassRunner.class)
@ComponentScan(basePackages = "cn.schoolwow.quickbeans.signer")
public class QuickBeansTest {
    @Resource
    private List<Signer> signers;

    @Test
    public void test(){
        System.out.println(signers);
    }
}