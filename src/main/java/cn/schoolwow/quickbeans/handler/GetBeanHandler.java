package cn.schoolwow.quickbeans.handler;

import cn.schoolwow.quickbeans.annotation.Scheduled;
import cn.schoolwow.quickbeans.annotation.ScopeType;
import cn.schoolwow.quickbeans.domain.BeanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**获取Bean*/
public class GetBeanHandler implements GetBean{
    private Logger logger = LoggerFactory.getLogger(GetBeanHandler.class);

    private Map<String,BeanContext> beanContextMap;
    private ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();

    public GetBeanHandler(Map<String,BeanContext> beanContextMap){
        this.beanContextMap = beanContextMap;
    }

    /**获取BeanContext映射*/
    public Map<String, BeanContext> getBeanContextMap() {
        return beanContextMap;
    }

    /**获取Bean*/
    public Object getBean(String name){
        return doGetBean(name,Object.class);
    }

    /**获取Bean*/
    public <T> T getBean(Class<T> clazz){
        return getBean(clazz.getName(),clazz);
    }

    /**获取Bean*/
    public <T> T getBean(String name, Class<T> clazz){
        return doGetBean(name,clazz);
    }

    /**获取Bean*/
    public List getBeanList(String name){
        return doGetBeanList(name,Object.class);
    }

    /**获取Bean*/
    public <T> List<T> getBeanList(Class<T> clazz){
        return doGetBeanList(clazz.getName(),clazz);
    }

    /**获取Bean*/
    public List<String> getBeanNameList(){
        List<String> beanNameList = new ArrayList<>();
        for(BeanContext beanContext:beanContextMap.values()){
            beanNameList.addAll(beanContext.nameList);
        }
        return beanNameList;
    }

    /**获取容器中所有类*/
    public List<Class> getBeanClassList(){
        List<Class> classList = new ArrayList<>();
        for(BeanContext beanContext:beanContextMap.values()){
            classList.add(beanContext.clazz);
        }
        return classList;
    }

    private <T> T doGetBean(String name,Class<T> clazz) {
        List<T> beanList = doGetBeanList(name,clazz);
        return beanList.isEmpty()?null:beanList.get(0);
    }

    private <T> List<T> doGetBeanList(String name,Class<T> clazz) {
        List<T> beanList = new ArrayList<>();
        for(BeanContext beanContext:beanContextMap.values()){
            if(!beanContext.nameList.contains(name)){
                continue;
            }
            switch(beanContext.scopeType){
                case singleton:{
                }break;
                case prototype:{
                    try {
                        instantiation(beanContext);
                        inject(beanContext,false);
                        initialize(beanContext);
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }break;
            }
            beanList.add(clazz.cast(beanContext.instance));
        }
        return beanList;
    }

    /**单例实例化*/
    public void instantiation(BeanContext beanContext){
        try {
            if(null!=beanContext.method){
                Parameter[] parameters = beanContext.method.getParameters();
                Object[] parameterValues = new Object[parameters.length];
                for(int i=0;i<parameters.length;i++){
                    parameterValues[i] = getBean(parameters[i].getName());
                    if(null==parameterValues[i]){
                        parameterValues[i] = getBean(parameters[i].getType().getName());
                    }
                }
                if(parameters.length>0){
                    beanContext.instance = beanContext.method.invoke(beanContext.beanInstance,parameterValues);
                }else{
                    beanContext.instance = beanContext.method.invoke(beanContext.beanInstance);
                }
            }else if(null!=beanContext.clazz){
                if(null==beanContext.constructArguments){
                    beanContext.instance = beanContext.clazz.newInstance();
                }else{
                    Class[] constructArgumentClass = new Class[beanContext.constructArguments.length];
                    for(int i=0;i<constructArgumentClass.length;i++){
                        constructArgumentClass[i] = beanContext.constructArguments[i].getClass();
                    }
                    beanContext.instance = beanContext.clazz.getConstructor(constructArgumentClass).newInstance();
                }
            }
            beanContext.hasRegistered = true;
            logger.trace("[实例化]类名:{}",beanContext.clazz.getName());
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[实例化失败]类名:{},构造参数:{}",beanContext.clazz.getName(),beanContext.constructArguments);
        }
    }

    /**
     * 注入依赖
     * @param beanContext 上下文
     * @param ignorePrototype 是否忽略原型模式变量
     * */
    public void inject(BeanContext beanContext,boolean ignorePrototype) {
        Class superClass = beanContext.clazz;
        List<Field> fieldList = new ArrayList<>();
        while(null!=superClass){
            fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }
        Field[] fields = fieldList.toArray(new Field[0]);
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
            if(ignorePrototype&&beanContext.scopeType.equals(ScopeType.prototype)){
                continue;
            }
            if(field.getType().isArray()||field.getType().getName().equals("java.util.List")
                    ||field.getType().getName().equals("java.util.Set")){
                Collection beanList = null;
                //先根据resource定义的name
                String resourceName = resource.name();
                if(!resourceName.isEmpty()){
                    beanList = getBeanList(resourceName);
                }
                //根据成员变量名
                if(null==beanList||beanList.isEmpty()){
                    resourceName = field.getName();
                    beanList = getBeanList(resourceName);
                }
                //根据类型名
                if(beanList==null||beanList.size()==0){
                    Type type = field.getGenericType();
                    if(type instanceof ParameterizedType){
                        ParameterizedType pType = (ParameterizedType)type;
                        Type claz = pType.getActualTypeArguments()[0];
                        if(claz instanceof Class){
                            resourceName = ((Class) claz).getName();
                            beanList = getBeanList(resourceName);
                        }
                    }else{
                        beanList = getBeanList(type.getTypeName().substring(0,type.getTypeName().length()-2));
                    }
                }
                if(null!=beanList&&!beanList.isEmpty()){
                    try {
                        field.set(beanContext.instance,beanList);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else{
                    throw new IllegalArgumentException("[注入依赖失败]该依赖不存在!类名:"+beanContext.clazz.getName()+",依赖:"+field.getName());
                }
            }else{
                //先根据resource定义的name
                Object bean = null;
                String resourceName = resource.name();
                if(!resourceName.isEmpty()){
                    bean = getBean(resourceName);
                }
                //根据成员变量名
                if(bean==null){
                    resourceName = field.getName();
                    bean = getBean(resourceName);
                }
                //根据类型名
                if(bean==null){
                    resourceName = field.getType().getName();
                    bean = getBean(resourceName);
                }
                if(null!=bean){
                    try {
                        field.set(beanContext.instance,bean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else{
                    throw new IllegalArgumentException("[注入依赖失败]该依赖不存在!类名:"+beanContext.clazz.getName()+",依赖:"+field.getName());
                }
            }
        }
        beanContext.hasInject = true;
        logger.trace("[注入依赖]类名:{}",beanContext.clazz.getName());
    }

    /**初始化*/
    public void initialize(BeanContext beanContext) {
        if(beanContext.initMethod!=null){
            try {
                beanContext.initMethod.invoke(beanContext.instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        logger.trace("[初始化]类名:{},名称:{}",beanContext.clazz.getName(),beanContext.nameList);
        if(!beanContext.scopeType.equals(ScopeType.singleton)){
            return;
        }
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
