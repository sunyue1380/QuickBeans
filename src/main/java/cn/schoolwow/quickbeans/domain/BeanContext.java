package cn.schoolwow.quickbeans.domain;

import cn.schoolwow.quickbeans.annotation.ScopeType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**Bean信息*/
public class BeanContext {
    /**名称*/
    public Set<String> nameList = new HashSet<>();
    /**范围类型*/
    public ScopeType scopeType = ScopeType.singleton;
    /**构造参数*/
    public Object[] constructArguments;
    /**单例*/
    public Object instance;
    /**所在类*/
    public Class clazz;
    /**Bean注解所在类*/
    public Class beanClazz;
    /**Bean注解所在方法*/
    public Method method;
    /**初始化方法*/
    public Method initMethod;
    /**销毁方法*/
    public Method destroyMethod;
    /**定时任务方法*/
    public List<Method> scheduledMethodList = new ArrayList<>();
    /**是否已经注册过*/
    public boolean hasRegistered;
    /**依赖是否已注入*/
    public boolean hasInject;
    /**是否已经解析过ComponentScan*/
    public boolean hasComponentScaned;
}
