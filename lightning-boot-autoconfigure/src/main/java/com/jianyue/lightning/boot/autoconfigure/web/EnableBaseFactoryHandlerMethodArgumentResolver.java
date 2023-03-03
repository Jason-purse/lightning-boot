package com.jianyue.lightning.boot.autoconfigure.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author FLJ
 * @date 2023/3/2
 * @time 12:01
 * @Description 参数解析器实现 ..
 * 启动基于{@link com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerMethodArgumentResolver}
 * 的参数解析器 !!!
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HandlerMethodArgumentResolverConfiguration.class)
public @interface EnableBaseFactoryHandlerMethodArgumentResolver {
}
