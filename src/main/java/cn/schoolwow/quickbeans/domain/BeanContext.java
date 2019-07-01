package cn.schoolwow.quickbeans.domain;

import cn.schoolwow.quickbeans.annotation.ScopeType;

import java.lang.reflect.Method;

/**Bean信息*/
public class BeanContext {
    /**范围类型*/
    public ScopeType scopeType = ScopeType.singleton;
    /**单例*/
    public Object instance;
    /**所在类*/
    public Class _class;
    /**所在方法*/
    public Method method;
    /**依赖是否已注入*/
    public boolean hasInject;

    @Override
    public String toString() {
        return "BeanContext{" +
                "scopeType=" + scopeType +
                ", instance=" + instance +
                ", _class=" + _class +
                ", method=" + method +
                ", hasInject=" + hasInject +
                '}';
    }
}
