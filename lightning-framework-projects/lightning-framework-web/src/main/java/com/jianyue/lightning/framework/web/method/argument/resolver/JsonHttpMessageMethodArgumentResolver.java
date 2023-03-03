package com.jianyue.lightning.framework.web.method.argument.resolver;

import com.jianyue.lightning.boot.starter.util.factory.TransformHandler;

/**
 * @author FLJ
 * @date 2023/3/3
 * @time 16:50
 * @Description json http message method argument resolver
 */
public interface JsonHttpMessageMethodArgumentResolver extends TransformHandler<HttpMessageContext,Object> {

}
