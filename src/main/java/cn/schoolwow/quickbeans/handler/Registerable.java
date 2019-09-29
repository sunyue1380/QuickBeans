package cn.schoolwow.quickbeans.handler;

/**注册接口*/
public interface Registerable {
    void register(Class... annotatedClasses);

    void scan(String... basePackages);

    <T> void registerBean(Class<T> annotatedClass, Object... constructorArguments);

    <T> void registerBean(String beanName, Class<T> annotatedClass, Object... constructorArguments);
}
