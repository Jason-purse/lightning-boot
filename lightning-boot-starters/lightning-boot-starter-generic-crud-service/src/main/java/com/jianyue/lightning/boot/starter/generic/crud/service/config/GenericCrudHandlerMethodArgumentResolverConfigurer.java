package com.jianyue.lightning.boot.starter.generic.crud.service.config;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.SimpleForGenericCrudHandlerMethodArgumentResolverHandler;

/**
 * @author FLJ
 * @date 2023/3/2
 * @time 12:23
 * @Description 抽象crud 处理器方法参数解析器配置器
 */
public interface GenericCrudHandlerMethodArgumentResolverConfigurer {

    void configure(SimpleForGenericCrudHandlerMethodArgumentResolverHandler handler);
}
