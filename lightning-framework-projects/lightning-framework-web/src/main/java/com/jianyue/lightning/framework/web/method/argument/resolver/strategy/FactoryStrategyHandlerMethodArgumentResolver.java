package com.jianyue.lightning.framework.web.method.argument.resolver.strategy;

import com.jianyue.lightning.framework.web.annotations.method.ArgumentFactoryStrategy;
import com.jianyue.lightning.framework.web.annotations.method.ArgumentResolveStrategy;
import com.jianyue.lightning.framework.web.annotations.method.ArgumentStrategy;
import com.jianyue.lightning.framework.web.method.argument.resolver.FirstClassSupportHandlerMethodArgumentResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * 实现基于工厂的方式进行解析 !!!!
 * <p>
 * 对于需要特殊处理的接口参数来进行 工厂策略化解析 !!!
 * <p>
 * 如果需要增加特定的工厂方法参数解析器,可以通过{@link FactoryStrategySupport#addResolvers(List)} 进行增加 !!!
 */
public class FactoryStrategyHandlerMethodArgumentResolver implements FirstClassSupportHandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return !ClassUtils.isPrimitiveOrWrapper(parameter.getParameterType())
                && (assertArgumentStrategyIsSpi(parameter.getParameter())) ||
                assertArgumentStrategyIsSpi(parameter.getParameterType())
                ;
    }

    private boolean assertArgumentStrategyIsSpi(AnnotatedElement parameter) {
        ArgumentStrategy annotation = AnnotationUtils.findAnnotation(parameter, ArgumentStrategy.class);
        if (annotation != null) {
            Class<? extends ArgumentResolveStrategy> value = annotation.value();
            return value == ArgumentResolveStrategy.FACTORY;
        }
        return false;
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HandlerMethodArgumentResolverComposite resolvers = FactoryStrategySupport.getResolvers();
        if (resolvers.supportsParameter(parameter)) {
            return resolvers.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }

        return null;
    }

}
