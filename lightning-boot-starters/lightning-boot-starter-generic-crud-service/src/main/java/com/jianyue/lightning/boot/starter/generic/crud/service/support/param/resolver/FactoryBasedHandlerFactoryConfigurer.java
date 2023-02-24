package com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver;

import com.jianyue.lightning.util.JsonUtil;

/**
 * @author FLJ
 * @date 2023/2/23
 * @time 10:56
 * @Description 用来追加handler 方法参数解析器 !!!
 */
public interface FactoryBasedHandlerFactoryConfigurer {

    void configMethodArgumentResolver(FactoryBasedHandlerMethodArgumentResolver methodArgumentResolver);

    /**
     * 配置 jsonutil
     * @param jsonUtil
     * @see SimpleForGenericCrudHandlerMethodArgumentResolverHandler
     */
    void configJsonUtil(JsonUtil jsonUtil);
}
