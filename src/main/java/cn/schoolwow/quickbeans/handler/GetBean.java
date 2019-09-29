package cn.schoolwow.quickbeans.handler;

import cn.schoolwow.quickbeans.domain.BeanContext;

import java.util.List;
import java.util.Map;

public interface GetBean {
    /**获取BeanContext映射*/
    Map<String, BeanContext> getBeanContextMap();

    /**获取Bean*/
    Object getBean(String name);

    /**获取Bean*/
    <T> T getBean(Class<T> clazz);

    /**获取Bean*/
    <T> T getBean(String name, Class<T> clazz);

    /**获取Bean*/
    List getBeanList(String name);

    /**获取Bean*/
    <T> List<T> getBeanList(Class<T> clazz);

    /**获取Bean*/
    List<String> getBeanNameList();

    /**获取容器中所有类*/
    List<Class> getBeanClassList();
}
