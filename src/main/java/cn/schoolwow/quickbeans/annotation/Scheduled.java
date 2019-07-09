package cn.schoolwow.quickbeans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Scheduled {
    /**初始延迟(毫秒)*/
    long initialDelay() default 0;
    /**固定间隔执行(毫秒)*/
    long fixedDelay();
}
