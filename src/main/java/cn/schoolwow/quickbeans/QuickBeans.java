package cn.schoolwow.quickbeans;

import cn.schoolwow.quickbeans.domain.BeanContext;
import cn.schoolwow.quickbeans.handler.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickBeans implements Registerable,GetBean {
    private final Map<String,BeanContext> beanContextMap = new HashMap<>();
    private GetBeanHandler getBeanHandler = new GetBeanHandler(beanContextMap);
    private Registerable registerable = new RegisterHandler(beanContextMap);
    private RefreshHandler refreshHandler = new RefreshHandler(getBeanHandler,registerable);

    @Override
    public Map<String, BeanContext> getBeanContextMap() {
        return getBeanHandler.getBeanContextMap();
    }

    @Override
    public Object getBean(String name) {
        return getBeanHandler.getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return getBeanHandler.getBean(clazz);
    }

    @Override
    public <T> T getBean(String name,Class<T> clazz) {
        return getBeanHandler.getBean(name,clazz);
    }

    @Override
    public List getBeanList(String name) {
        return getBeanHandler.getBeanList(name);
    }

    @Override
    public <T> List<T> getBeanList(Class<T> clazz) {
        return getBeanHandler.getBeanList(clazz);
    }

    @Override
    public List<String> getBeanNameList() {
        return getBeanHandler.getBeanNameList();
    }

    @Override
    public List<Class> getBeanClassList() {
        return getBeanHandler.getBeanClassList();
    }

    @Override
    public void register(Class... annotatedClasses) {
        registerable.register(annotatedClasses);
    }

    @Override
    public void scan(String... basePackages) {
        registerable.scan(basePackages);
    }

    @Override
    public <T> void registerBean(Class<T> annotatedClass, Object... constructorArguments) {
        registerable.registerBean(annotatedClass,constructorArguments);
    }

    @Override
    public <T> void registerBean(String beanName, Class<T> annotatedClass, Object... constructorArguments) {
        registerable.registerBean(beanName,annotatedClass,constructorArguments);
    }

    public void refresh(){
        refreshHandler.refresh(true);
    }
}
