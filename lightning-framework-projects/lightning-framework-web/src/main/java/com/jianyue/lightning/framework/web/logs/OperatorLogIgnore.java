package com.jianyue.lightning.framework.web.logs;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author FLJ
 * @date 2022/8/11
 * @time 9:52
 * @Description 操作日志忽略 注解
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperatorLogIgnore {

    // 标记注解,只需要标记即可 ..
    @AliasFor("methods")
    String[] value() default {};

    /**
     * ignore methods
     * 仅当处于类级别的注释才需要这个 ..
     */
    @AliasFor("value")
    String[] methods() default {};
}
