package com.jianyue.lightning.framework.web.method.argument.resolver.enhance;

import com.jianyue.lightning.framework.web.method.argument.context.MethodArgumentContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author FLJ
 * @date 2023/2/23
 * @time 10:31
 */
public class EnhanceHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {


    private final HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();

    private final HandlerMethodArgumentEnhancerComposite enhancers = new HandlerMethodArgumentEnhancerComposite();


    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return resolvers.supportsParameter(parameter);
    }


    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object argument = resolvers.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        MethodArgumentContext context = new MethodArgumentContext(
                parameter, mavContainer,
                webRequest, binderFactory,
                argument
        );
        // 进行参数解析 !!!
        // 不判断空参数 ..
        if (enhancers.supportsParameter(parameter)) {
            enhancers.enhanceArgument(context);
        }

        return context.getTarget();
    }


    public final void addHandlerMethodArgumentResolvers(HandlerMethodArgumentResolver ... resolvers) {
        this.resolvers.addResolvers(resolvers);
    }

    public final void addHandlerMethodArgumentEnhancers(HandlerMethodArgumentEnhancer... resolvers) {
        this.enhancers.addEnhancers(resolvers);
    }
}
