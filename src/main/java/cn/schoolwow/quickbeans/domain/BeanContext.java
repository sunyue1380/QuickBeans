package cn.schoolwow.quickbeans.domain;

import cn.schoolwow.quickbeans.annotation.ScopeType;

/**Bean信息*/
public class BeanContext {
    /**范围类型*/
    public ScopeType scopeType = ScopeType.singleton;
    /**单例*/
    public Object instance;
    /**类*/
    public Class _class;
    /**名称*/
    public String name;

    @Override
    public String toString() {
        return "BeanContext{" +
                "scopeType=" + scopeType +
                ", instance=" + instance +
                ", _class=" + _class +
                ", name='" + name + '\'' +
                '}';
    }
}
