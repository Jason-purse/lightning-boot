package com.jianyue.lightning.framework.web.annotations.method;

import java.lang.annotation.*;

/**
 * 参数策略, 通过给定的type 来实例化并调用它的type 获取对应的系统支持的参数解析器来解析 参数 !!!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface ArgumentStrategy {

    Class<? extends ArgumentResolveStrategy> value();
}
