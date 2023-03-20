package com.jianyue.lightning.framework.web.annotations.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数工厂策略 !!!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ArgumentStrategy(FactoryArgumentResolveStrategy.class)
public @interface ArgumentFactoryStrategy {

}
