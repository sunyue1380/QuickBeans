package cn.schoolwow.quickbeans.domain;

import cn.schoolwow.quickbeans.annotation.ScopeType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**Bean信息*/
public class BeanContext {
    /**名称*/
    public List<String> nameList;
    /**范围类型*/
    public ScopeType scopeType = ScopeType.singleton;
    /**单例*/
    public Object instance;
    /**所在类*/
    public Class _class;
    /**所在方法*/
    public Method method;
    /**初始化方法*/
    public Method initMethod;
    /**定时任务方法*/
    public List<Method> scheduledMethodList = new ArrayList<>();
    /**销毁方法*/
    public Method destroyMethod;
    /**依赖是否已注入*/
    public boolean hasInject;
    /**初始化是否已调用过*/
    public boolean hasInitialized;
}
