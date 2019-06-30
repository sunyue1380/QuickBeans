package cn.schoolwow.quickbeans;

import cn.schoolwow.quickbeans.annotation.*;
import cn.schoolwow.quickbeans.domain.BeanContext;
import cn.schoolwow.quickbeans.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class QuickBeans {
    private static Logger logger = LoggerFactory.getLogger(QuickBeans.class);
    private final Map<String,BeanContext> beanMap = new HashMap<>();

    /**获取Bean*/
    public Object getBean(String name){
        try {
            return doGetBean(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**获取Bean*/
    public <T> T getBean(Class<T> _class ,String name){
        try {
            return _class.cast(doGetBean(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**获取Bean*/
    public Set<String> getBeanNameSet(){
        return beanMap.keySet();
    }

    /**依赖注入*/
    public void refresh(){
        try {
            Set<String> nameSet = beanMap.keySet();
            for(String name:nameSet){
                Object instance = beanMap.get(name);
                doInject(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**扫描类*/
    public void register(Class _class){
        try {
            doScan(_class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**扫描包名*/
    public void scan(String... packageNames){
        try {
            doScan(packageNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object doGetBean(String name) throws Exception{
        BeanContext beanContext = beanMap.get(name);
        if(beanContext==null){
            return null;
        }
        if(beanContext.scopeType.equals(ScopeType.singleton)){
            return beanContext.instance;
        }else{
            Object instance = beanContext._class.newInstance();
            doInject(instance);
            return instance;
        }
    }

    /**扫描包名,注入依赖*/
    private void doScan(String... packageNames) throws Exception {
        List<Class> classList = new ArrayList<>();
        for(String packageName:packageNames){
            classList.addAll(PackageUtil.scanPackage(packageName));
        }
        for(Class c:classList){
            doScan(c);
        }
    }

    /**扫描包名*/
    private void doScan(Class c) throws Exception {
        handleComponent(c);
        handleBean(c);
        handleComponentScan(c);
    }

    /**注入依赖*/
    private void doInject(Object instance) throws Exception {
        Class c = instance.getClass();
        Field[] fields = c.getDeclaredFields();
        Field.setAccessible(fields,true);
        for(Field field:fields){
            int modifiers = field.getModifiers();
            if(Modifier.isStatic(modifiers)||Modifier.isFinal(modifiers)){
                continue;
            }
            //判断是否有Resource注解
            Resource resource = field.getAnnotation(Resource.class);
            if(resource==null){
                continue;
            }
            String resourceName = resource.name();
            if(resourceName==null||resourceName.isEmpty()){
                resourceName = field.getType().getName();
            }
            logger.debug("[依赖注入]类名:{},成员变量:{}",c.getName(),field.getName());
            field.set(instance,doGetBean(resourceName));
        }
    }

    /**处理ComponentScan注解*/
    private void handleComponentScan(Class c) throws Exception {
        ComponentScan componentScan = (ComponentScan) c.getAnnotation(ComponentScan.class);
        if(componentScan==null){
            return;
        }
        String[] basePackages = componentScan.basePackages();
        doScan(basePackages);
        Class[] classList = componentScan.basePackageClasses();
        for(Class _class:classList){
            doScan(_class);
        }
    }

    /**处理Bean注解*/
    private void handleBean(Class c) throws Exception {
        Method[] methods = c.getDeclaredMethods();
        for(Method method:methods){
            Bean bean = method.getDeclaredAnnotation(Bean.class);
            if(bean==null){
                continue;
            }
            BeanContext beanContext = new BeanContext();
            beanContext._class = c;
            beanContext.instance = method.invoke(c.newInstance());
            beanContext.name = bean.name();
            if(beanContext.name.isEmpty()){
                beanContext.name = c.getName();
            }
            Scope scope = method.getDeclaredAnnotation(Scope.class);
            if(scope!=null){
                beanContext.scopeType = scope.value();
            }
            beanMap.put(beanContext.name,beanContext);
            beanMap.put(method.getReturnType().getName(),beanContext);
            logger.debug("[新建实例]名称:{},实例:{}",beanContext.name,beanContext);
        }
    }

    /**处理Component注解*/
    private void handleComponent(Class c) throws IllegalAccessException, InstantiationException {
        //检查是否有Component注解
        Component component = (Component) c.getDeclaredAnnotation(Component.class);
        if(component==null){
            return;
        }
        BeanContext beanContext = new BeanContext();
        beanContext._class = c;
        beanContext.instance = c.newInstance();
        beanContext.name = component.name();
        if(beanContext.name.isEmpty()){
            beanContext.name = c.getName();
        }
        Scope scope = (Scope) c.getDeclaredAnnotation(Scope.class);
        if(scope!=null){
            beanContext.scopeType = scope.value();
        }
        beanMap.put(beanContext.name,beanContext);
        beanMap.put(c.getName(),beanContext);
        logger.debug("[新建实例]名称:{},实例:{}",beanContext.name,beanContext);
    }
}
