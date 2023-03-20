package com.jianyue.lightning.framework.web.annotations.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spi 方法参数解析策略 !!!
 *
 * 将根据参数的类型进行类路径解析 !!!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ArgumentStrategy(SpiArgumentResolveStrategy.class)
public @interface ArgumentSpiStrategy {

}
