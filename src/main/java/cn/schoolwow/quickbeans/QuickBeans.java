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
    /**存放对象*/
    private final Map<String,BeanContext> beanMap = new HashMap<>();
    /**当前是否是刷新状态*/
    private boolean isRefresh = false;

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
    public <T> T getBean(Class<T> _class){
        try {
            return _class.cast(doGetBean(_class.getName()));
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
            isRefresh = true;
            for(BeanContext beanContext:beanMap.values()){
                if(beanContext.scopeType.equals(ScopeType.singleton)&&!beanContext.hasInject){
                    doInject(beanContext.instance);
                    beanContext.hasInject = true;
                }
            }
            isRefresh = false;
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
    public void doScan(String packageName){
        try {
            for(Class c:PackageUtil.scanPackage(packageName)){
                doScan(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**扫描包名*/
    public void scan(String... packageNames){
        try {
            List<Class> classList = new ArrayList<>();
            for(String packageName:packageNames){
                classList.addAll(PackageUtil.scanPackage(packageName));
            }
            for(Class c:classList){
                doScan(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**实际获取Bean的方法*/
    private Object doGetBean(String name) throws Exception{
        BeanContext beanContext = beanMap.get(name);
        if(beanContext==null){
            return null;
        }
        //原型模式创建
        Object instance = null;
        if(beanContext.scopeType.equals(ScopeType.prototype)){
            if(beanContext.method!=null){
                instance = beanContext.method.invoke(beanContext._class.newInstance());
            }else if(beanContext._class!=null){
                instance = beanContext._class.newInstance();
            }
            logger.debug("[原型-新建实例]名称:{},实例:{}",name,instance);
            doInitialized(instance,beanContext);
        }else{
            instance = beanContext.instance;
        }
        doInject(instance);
        return instance;
    }

    /**扫描类*/
    private void doScan(Class c) throws Exception {
        handleComponent(c);
        handleBean(c);
        handleComponentScan(c);
    }

    /**创建实例*/
    private void doCreate(String[] names,BeanContext beanContext) throws Exception {
        for(String name:names){
            beanMap.put(name,beanContext);
        }
        if(beanContext.scopeType.equals(ScopeType.singleton)){
            if(beanContext.method!=null){
                beanContext.instance = beanContext.method.invoke(beanContext._class.newInstance());
            }else if(beanContext._class!=null){
                beanContext.instance = beanContext._class.newInstance();
            }
            logger.debug("[新建实例]名称:{},实例:{}",names,beanContext.instance);
            doInitialized(beanContext.instance,beanContext);
        }
    }

    //初始化
    private void doInitialized(Object instance,BeanContext beanContext) throws Exception{
        if(beanContext.initMethod!=null){
            beanContext.initMethod.invoke(instance);
        }
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
            Resource resource = field.getDeclaredAnnotation(Resource.class);
            if(resource==null){
                continue;
            }
            BeanContext beanContext = beanMap.get(field.getType().getName());
            //刷新状态下忽略原型模式成员变量
            if(isRefresh&&beanContext.scopeType.equals(ScopeType.prototype)){
                continue;
            }
            //单例模式且值为null或者原型模式时创建实例
            if((beanContext.scopeType.equals(ScopeType.singleton)&&field.get(instance)==null
            )||beanContext.scopeType.equals(ScopeType.prototype)){
                Object bean = null;
                //先根据resource定义的name
                String resourceName = resource.name();
                if(!resourceName.isEmpty()){
                    bean = doGetBean(resourceName);
                }
                //根据成员变量名
                if(bean==null){
                    resourceName = field.getName();
                    bean = doGetBean(resourceName);
                }
                //根据类型名
                if(bean==null){
                    resourceName = field.getType().getName();
                    bean = doGetBean(resourceName);
                }

                if(bean==null){
                    throw new IllegalArgumentException("依赖为空!类名:"+c.getName()+",依赖:"+field.getName());
                }
                logger.debug("[依赖注入]类名:{},成员变量:{}",c.getName(),field.getName());
                field.set(instance,bean);
            }
        }
    }

    /**处理ComponentScan注解*/
    private void handleComponentScan(Class c) throws Exception {
        ComponentScan componentScan = (ComponentScan) c.getAnnotation(ComponentScan.class);
        if(componentScan==null){
            return;
        }
        String[] basePackages = componentScan.basePackages();
        for(String basePackage:basePackages){
            doScan(basePackage);
        }
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
            beanContext.method = method;
            Scope scope = method.getDeclaredAnnotation(Scope.class);
            if(scope!=null){
                beanContext.scopeType = scope.value();
            }
            //处理初始化方法和销毁方法
            if(!bean.initMethod().isEmpty()){
                beanContext.initMethod = method.getReturnType().getDeclaredMethod(bean.initMethod());
            }
            if(!bean.destroyMethod().isEmpty()){
                beanContext.destroyMethod = method.getReturnType().getDeclaredMethod(bean.destroyMethod());
            }
            List<String> nameList = new ArrayList<>();
            if(!bean.name().isEmpty()){
                nameList.add(bean.name());
            }
            nameList.add(method.getName());
            nameList.add(method.getReturnType().getName());
            doCreate(nameList.toArray(new String[0]),beanContext);
        }
    }

    /**处理Component注解*/
    private void handleComponent(Class c) throws Exception {
        Component component = (Component) c.getDeclaredAnnotation(Component.class);
        if(component==null){
            return;
        }
        BeanContext beanContext = new BeanContext();
        beanContext._class = c;
        Scope scope = (Scope) c.getDeclaredAnnotation(Scope.class);
        if(scope!=null){
            beanContext.scopeType = scope.value();
        }
        List<String> nameList = new ArrayList<>();
        if(!component.name().isEmpty()){
            nameList.add(component.name());
        }
        Class[] classes = c.getInterfaces();
        for(Class _class:classes){
            nameList.add(_class.getName());
        }
        nameList.add(c.getName());
        doCreate(nameList.toArray(new String[0]),beanContext);
    }
}
