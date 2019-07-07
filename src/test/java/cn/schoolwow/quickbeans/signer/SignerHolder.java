package cn.schoolwow.quickbeans.signer;

import cn.schoolwow.quickbeans.annotation.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Component
public class SignerHolder {
    private Logger logger = LoggerFactory.getLogger(SignerHolder.class);
    @Resource(name = "signer")
    public List<Object> signerList;

    @PostConstruct
    public void init(){
        logger.info("[加载signerList]{}",signerList);
    }
}
