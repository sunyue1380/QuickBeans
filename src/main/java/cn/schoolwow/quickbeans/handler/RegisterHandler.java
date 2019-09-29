package cn.schoolwow.quickbeans.handler;

import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.annotation.ComponentScan;
import cn.schoolwow.quickbeans.domain.BeanContext;
import cn.schoolwow.quickbeans.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**注册类*/
public class RegisterHandler implements Registerable {
    private Logger logger = LoggerFactory.getLogger(Registerable.class);
    private Map<String,BeanContext> beanContextMap = new HashMap<>();

    public RegisterHandler(Map<String,BeanContext> beanContextMap){
        this.beanContextMap = beanContextMap;
    }

    public void register(Class... annotatedClasses) {
        for(Class clazz:annotatedClasses){
            if(beanContextMap.containsKey(clazz.getName())){
                continue;
            }
            if(null==clazz.getAnnotation(Component.class)&&null==clazz.getAnnotation(ComponentScan.class)){
                continue;
            }
            BeanContext beanContext = new BeanContext();
            beanContext.clazz = clazz;
            beanContextMap.put(clazz.getName(),beanContext);
        }
    }

    public void scan(String... basePackages) {
        for(String basePackage:basePackages){
            List<Class> classList = PackageUtil.scanPackage(basePackage);
            register(classList.toArray(new Class[0]));
        }
    }

    public <T> void registerBean(Class<T> annotatedClass, Object... constructorArguments) {
        this.registerBean(null, annotatedClass, constructorArguments);
    }

    public <T> void registerBean(String beanName, Class<T> annotatedClass, Object... constructorArguments) {
        BeanContext beanContext = beanContextMap.get(annotatedClass.getName());
        if(null==beanContext){
            beanContext = new BeanContext();
            beanContext.clazz = annotatedClass;
            beanContextMap.put(annotatedClass.getName(),beanContext);
        }
        if(null!=beanName&&beanName.isEmpty()){
            beanContext.nameList.add(beanName);
        }
        beanContext.constructArguments = constructorArguments;
    }
}
