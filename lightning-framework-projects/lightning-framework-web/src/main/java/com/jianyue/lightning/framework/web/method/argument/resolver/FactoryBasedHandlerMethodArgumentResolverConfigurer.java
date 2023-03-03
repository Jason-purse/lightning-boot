package com.jianyue.lightning.framework.web.method.argument.resolver;

/**
 * @author FLJ
 * @date 2023/2/23
 * @time 10:56
 * @Description 用来追加handler 方法参数解析器 !!!
 */
public interface FactoryBasedHandlerMethodArgumentResolverConfigurer {

    void configMethodArgumentResolver(FactoryBasedHandlerMethodArgumentResolver methodArgumentResolver);
}
