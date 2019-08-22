package cn.schoolwow.quickbeans;

import cn.schoolwow.quickbeans.annotation.*;
import cn.schoolwow.quickbeans.domain.BeanContext;
import cn.schoolwow.quickbeans.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuickBeans {
    private static Logger logger = LoggerFactory.getLogger(QuickBeans.class);
    /**存放BeanContext列表*/
    private final List<BeanContext> beanContextList = new ArrayList<>();
    /**当前是否是刷新状态*/
    private boolean isRefresh = false;
    /**存在已经扫描过的类,防止重复扫描*/
    private List<String> scanedClassList = new ArrayList<>();
    /**定时任务线程池*/
    private ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();

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
            return getBean(_class,_class.getName());
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
    public List<Object> getBeanList(String name){
        try {
            return doGetBeanList(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**获取Bean*/
    public <T> List<T> getBeanList(Class<T> _class){
        return getBeanList(_class,_class.getName());
    }

    /**获取Bean*/
    public <T> List<T> getBeanList(Class<T> _class ,String name){
        try {
            List<Object> objectList = doGetBeanList(name);
            List<T> beanList = new ArrayList<>();
            for(Object o:objectList){
                beanList.add(_class.cast(o));
            }
            return beanList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**获取Bean*/
    public List<String> getBeanNameList(){
        List<String> beanNameList = new ArrayList<>();
        for(BeanContext beanContext:this.beanContextList){
            beanNameList.addAll(beanContext.nameList);
        }
        return beanNameList;
    }

    /**获取容器中所有类*/
    public List<Class> getBeanClassList(){
        List<Class> classList = new ArrayList<>();
        for(BeanContext beanContext:beanContextList){
            classList.add(beanContext._class);
        }
        return classList;
    }

    /**依赖注入*/
    public void refresh(){
        try {
            isRefresh = true;
            for(BeanContext beanContext:beanContextList){
                if(beanContext.scopeType.equals(ScopeType.singleton)&&!beanContext.hasInject){
                    doInject(beanContext.instance);
                    beanContext.hasInject = true;
                }
            }
            for(BeanContext beanContext:beanContextList){
                if(beanContext.scopeType.equals(ScopeType.singleton)){
                    doInitialized(beanContext.instance,beanContext);
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
        List<Object> beanList = doGetBeanList(name);
        if(beanList.size()>0){
            return beanList.get(0);
        }else{
            return null;
        }
    }

    /**实际获取Bean的方法*/
    private List<Object> doGetBeanList(String name) throws Exception{
        List<Object> beanList = new ArrayList<>();
        for(BeanContext beanContext:this.beanContextList){
            if(!beanContext.nameList.contains(name)){
                continue;
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
            }else{
                instance = beanContext.instance;
                if(!beanContext.hasInject){
                    doInject(beanContext.instance);
                }
            }
            doInject(instance);
            beanList.add(instance);
        }
        return beanList;
    }

    /**扫描类*/
    private void doScan(Class c) throws Exception {
        if(scanedClassList.contains(c.getName())){
            return;
        }
        scanedClassList.add(c.getName());
        handleComponent(c);
        handleBean(c);
        handleComponentScan(c);
    }

    /**创建实例*/
    private void doCreate(List<String> nameList,BeanContext beanContext) throws Exception {
        beanContext.nameList = nameList;
        if(beanContext.scopeType.equals(ScopeType.singleton)){
            if(beanContext.method!=null){
                beanContext.instance = beanContext.method.invoke(beanContext._class.newInstance());
            }else if(beanContext._class!=null){
                beanContext.instance = beanContext._class.newInstance();
            }
            logger.debug("[新建实例]名称:{},实例:{}",nameList,beanContext.instance);
        }
        this.beanContextList.add(beanContext);
    }

    //初始化
    private void doInitialized(Object instance,BeanContext beanContext) throws Exception{
        if(beanContext.initMethod!=null){
            beanContext.initMethod.invoke(instance);
        }
        //定时任务只对单例模式有效
        if(beanContext.scopeType.equals(ScopeType.singleton)){
            for(Method method:beanContext.scheduledMethodList){
                Scheduled scheduled = method.getDeclaredAnnotation(Scheduled.class);
                scheduledPool.scheduleWithFixedDelay(()->{
                    try {
                        method.invoke(beanContext.instance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                },scheduled.initialDelay(),scheduled.fixedDelay(), TimeUnit.MILLISECONDS);
            }
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
            BeanContext beanContext = getBeanContext(instance.getClass().getName());
            //刷新状态下忽略原型模式成员变量
            if(isRefresh&&beanContext!=null&&beanContext.scopeType.equals(ScopeType.prototype)){
                continue;
            }

            if(field.getType().isArray()||field.getType().getName().equals("java.util.List")){
                List<Object> bean = null;
                //先根据resource定义的name
                String resourceName = resource.name();
                if(!resourceName.isEmpty()){
                    bean = doGetBeanList(resourceName);
                }
                //根据成员变量名
                if(bean==null||bean.size()==0){
                    resourceName = field.getName();
                    bean = doGetBeanList(resourceName);
                }
                //根据类型名
                if(bean==null||bean.size()==0){
                    Type type = field.getGenericType();
                    if(type instanceof ParameterizedType){
                        ParameterizedType pType = (ParameterizedType)type;
                        Type claz = pType.getActualTypeArguments()[0];
                        if(claz instanceof Class){
                            resourceName = ((Class) claz).getName();
                            bean = doGetBeanList(resourceName);
                        }
                    }
                }
                if(bean!=null&&bean.size()>0){
                    if(field.getType().isArray()){
                        Object[] objects = bean.toArray(new Object[0]);
                        field.set(instance,objects);
                    }else{
                        field.set(instance,bean);
                    }
                }else{
                    throw new IllegalArgumentException("依赖为空!类名:"+c.getName()+",依赖:"+field.getName());
                }
            }else{
                //先根据resource定义的name
                Object bean = null;
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
                if(bean!=null){
                    field.set(instance,bean);
                }else{
                    throw new IllegalArgumentException("依赖为空!类名:"+c.getName()+",依赖:"+field.getName());
                }
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
            doCreate(nameList,beanContext);
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
        Method[] methods = c.getDeclaredMethods();
        for(Method method:methods){
            if(method.getAnnotation(PostConstruct.class)!=null){
                beanContext.initMethod = method;
            }
            Scheduled scheduled = method.getDeclaredAnnotation(Scheduled.class);
            if(scheduled!=null){
                beanContext.scheduledMethodList.add(method);
            }
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
        doCreate(nameList,beanContext);
    }

    private BeanContext getBeanContext(String name){
        for(BeanContext beanContext:this.beanContextList){
            if(beanContext.nameList.contains(name)){
                return beanContext;
            }
        }
        return null;
    }
}
