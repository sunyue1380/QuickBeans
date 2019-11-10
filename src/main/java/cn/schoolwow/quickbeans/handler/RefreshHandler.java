package cn.schoolwow.quickbeans.handler;

import cn.schoolwow.quickbeans.annotation.*;
import cn.schoolwow.quickbeans.domain.BeanContext;
import cn.schoolwow.quickbeans.test.QuickBeansJUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

/**容器刷新类*/
public class RefreshHandler {
    private Logger logger = LoggerFactory.getLogger(RefreshHandler.class);
    private static boolean inTest = false;
    static{
        try {
            Class.forName("org.junit.runner.RunWith");
            inTest = true;
        } catch (ClassNotFoundException e) {
            inTest = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private GetBeanHandler getBeanHandler;
    private Registerable registerable;

    public RefreshHandler(GetBeanHandler getBeanHandler, Registerable registerable) {
        this.getBeanHandler = getBeanHandler;
        this.registerable = registerable;
    }

    /**
     * 刷新容器
     * @param needInject 是否需要注入依赖
     * */
    public void refresh(boolean needInject){
        for(BeanContext beanContext:getBeanHandler.getBeanContextMap().values()){
            if(ScopeType.prototype.equals(beanContext.scopeType)||beanContext.hasRegistered){
                continue;
            }
            handleComponent(beanContext);
            handleBean(beanContext);
            handleComponentScan(beanContext);
        }
        if(needInject){
            for(BeanContext beanContext:getBeanHandler.getBeanContextMap().values()){
                if(beanContext.hasInject){
                    continue;
                }
                getBeanHandler.inject(beanContext,true);
            }
            for(BeanContext beanContext:getBeanHandler.getBeanContextMap().values()){
                getBeanHandler.initialize(beanContext);
            }
        }
    }

    /**处理Component注解*/
    private void handleComponent(BeanContext beanContext){
        Component component = (Component) beanContext.clazz.getDeclaredAnnotation(Component.class);
        if(null==component){
            if(inTest){
                RunWith runWith = (RunWith) beanContext.clazz.getAnnotation(RunWith.class);
                if(null==runWith||!QuickBeansJUnit4ClassRunner.class.getName().equals(runWith.value().getName())){
                    return;
                }
            }else{
                return;
            }
        }
        Scope scope = (Scope) beanContext.clazz.getDeclaredAnnotation(Scope.class);
        if(null!=scope){
            beanContext.scopeType = scope.value();
        }
        Method[] methods = beanContext.clazz.getDeclaredMethods();
        for(Method method:methods){
            if(method.getAnnotation(PostConstruct.class)!=null){
                beanContext.initMethod = method;
            }
            Scheduled scheduled = method.getDeclaredAnnotation(Scheduled.class);
            if(scheduled!=null){
                beanContext.scheduledMethodList.add(method);
            }
        }
        if(null!=component&&!component.name().isEmpty()){
            beanContext.nameList.add(component.name());
        }
        Class[] classes = beanContext.clazz.getInterfaces();
        if(null!=classes){
            for(Class _class:classes){
                beanContext.nameList.add(_class.getName());
            }
        }
        beanContext.nameList.add(beanContext.clazz.getName());
        getBeanHandler.instantiation(beanContext);
//        logger.debug("[handleComponent]类名:{}",beanContext.clazz.getName());
    }

    /**处理Bean注解*/
    private void handleBean(BeanContext beanContext){
        Method[] methods = beanContext.clazz.getDeclaredMethods();
        Class clazz = beanContext.clazz;
        for(Method method:methods){
            Bean bean = method.getDeclaredAnnotation(Bean.class);
            if(null==bean){
                continue;
            }
            beanContext = new BeanContext();
            beanContext.beanClazz = clazz;
            beanContext.clazz = method.getReturnType();
            if(getBeanHandler.getBeanContextMap().containsKey(beanContext.clazz.getName())){
                continue;
            }else{
                getBeanHandler.getBeanContextMap().put(beanContext.clazz.getName(),beanContext);
            }
            beanContext.method = method;
            Scope scope = method.getDeclaredAnnotation(Scope.class);
            if(scope!=null){
                beanContext.scopeType = scope.value();
            }
            //处理初始化方法和销毁方法
            try {
                if(!bean.initMethod().isEmpty()){
                    beanContext.initMethod = method.getReturnType().getDeclaredMethod(bean.initMethod());
                }
                if(!bean.destroyMethod().isEmpty()){
                    beanContext.destroyMethod = method.getReturnType().getDeclaredMethod(bean.destroyMethod());
                }
            }catch (Exception e){
                logger.warn("[设置初始化以及销毁办法失败]失败原因:{}",e.getMessage());
            }
            if(!bean.name().isEmpty()){
                beanContext.nameList.add(bean.name());
            }
            beanContext.nameList.add(method.getName());
            beanContext.nameList.add(method.getReturnType().getName());
            getBeanHandler.instantiation(beanContext);
//            logger.debug("[handleBean]类名:{}",beanContext.clazz.getName());
        }
    }

    /**处理ComponentScan注解*/
    private void handleComponentScan(BeanContext beanContext) {
        ComponentScan componentScan = (ComponentScan) beanContext.clazz.getAnnotation(ComponentScan.class);
        if(null==componentScan||beanContext.hasComponentScaned){
            return;
        }
        registerable.scan(componentScan.basePackages());
        registerable.register(componentScan.basePackageClasses());
        beanContext.hasComponentScaned = true;
        refresh(false);

//        logger.debug("[handleComponentScan]类名:{}",beanContext.clazz.getName());
    }
}
