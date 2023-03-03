package com.jianyue.lightning.framework.web.method.argument.resolver;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.function.Predicate;
/**
 * @author FLJ
 * @date 2023/3/2
 * @time 12:19
 * @Description  默认实现HandlerMethodArgumentResolver 而不是一个匿名类 !!
 */
@AllArgsConstructor
public class DefaultHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private Predicate<MethodParameter> predicate;
    private HandlerMethodArgumentResolverHandler handler;

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return predicate.test(parameter);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return handler.get(
                MethodArgumentContext.builder()
                        .methodParameter(parameter)
                        .mavContainer(mavContainer)
                        .request(webRequest)
                        .binderFactory(binderFactory)
                        .build()
        );
    }
}
