package cn.schoolwow.quickbeans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ComponentScan {
    /**待扫描包名*/
    String[] basePackages() default {};

    /**扫描类所在包*/
    Class<?>[] basePackageClasses() default {};
}
