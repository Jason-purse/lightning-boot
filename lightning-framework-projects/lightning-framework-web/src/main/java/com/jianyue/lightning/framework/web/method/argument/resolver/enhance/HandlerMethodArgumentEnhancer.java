package com.jianyue.lightning.framework.web.method.argument.resolver.enhance;

import com.jianyue.lightning.framework.web.method.argument.context.MethodArgumentContext;
import org.springframework.core.MethodParameter;

/**
 * lightning handler method argument resolver
 */
public interface HandlerMethodArgumentEnhancer {



    void enhanceArgument(MethodArgumentContext methodArgumentContext);

    boolean supportsParameter(MethodParameter methodParameter);
}
