package com.jianyue.lightning.framework.web.method.argument.resolver;

import com.jianyue.lightning.boot.starter.util.factory.TransformHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;

/**
 * @author FLJ
 * @date 2023/3/3
 * @time 16:50
 * @Description json http message method argument resolver
 */
public interface FactoryBasedJsonHMAMessageConverterHandler extends TransformHandler<HttpMessageContext, Object> {
    boolean supportsParameter(@NotNull MethodParameter parameter);
}
