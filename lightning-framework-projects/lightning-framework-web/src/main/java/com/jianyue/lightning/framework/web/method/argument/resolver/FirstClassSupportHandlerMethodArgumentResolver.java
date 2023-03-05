package com.jianyue.lightning.framework.web.method.argument.resolver;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
/**
 * 相比于 spring 默认的方法参数解析器执行的更早 !!!
 * 
 * 可选返回空,然后交给 spring 的默认参数器进行解析 !!!
 * 
 * 如果不返回空,则使用此返回结果 !!!
 */
public interface FirstClassSupportHandlerMethodArgumentResolver extends HandlerMethodArgumentResolver {
}
