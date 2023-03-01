package com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver;

import com.jianyue.lightning.boot.starter.generic.crud.service.util.ThreadLocalSupport;
import com.jianyue.lightning.boot.starter.util.OptionalFlow;
import com.jianyue.lightning.boot.starter.util.factory.Handler;
import com.jianyue.lightning.boot.starter.util.factory.HandlerFactory;
import com.jianyue.lightning.boot.starter.util.factory.HandlerProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author FLJ
 * @date 2023/2/23
 * @time 10:31
 * @Description 基于工厂的方法参数解析器 ...
 * @see com.jianyue.lightning.boot.starter.util.factory.HandlerFactory
 */
public class FactoryBasedHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final HandlerFactory handlerFactory = new HandlerFactory();

    private final ThreadLocalSupport<List<HandlerProvider>> providerSupport = ThreadLocalSupport.Companion.of();

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {

        List<HandlerProvider> handler = handlerFactory.getHandlers(parameter.getParameter().getType());
        List<HandlerProvider> handlerProviders = new ArrayList<>();
        return Boolean.TRUE.equals(
                OptionalFlow
                        .of(handler)
                        .map(getHandlerForSupport(parameter, handlerProviders))
                        .get()
        );
    }

    @NotNull
    private Function<List<HandlerProvider>, Boolean> getHandlerForSupport(@NotNull MethodParameter parameter, List<HandlerProvider> handlerProviders) {
        return handlers -> {
            for (HandlerProvider handlerProvider : handlers) {
                boolean support = handlerProvider.support(parameter);
                if (support) {
                    handlerProviders.add(handlerProvider);
                }
            }
            providerSupport.set(handlerProviders);
            return handlerProviders.size() > 0;
        };
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        List<HandlerProvider> handlerProvider = providerSupport.get();
        assert handlerProvider != null;
        MethodArgumentContext context = MethodArgumentContext
                .builder()
                .methodParameter(parameter)
                .mavContainer(mavContainer)
                .request(webRequest)
                .binderFactory(binderFactory)
                .build();

        for (HandlerProvider provider : handlerProvider) {
            Object value = provider
                    .getHandler()
                    .<HandlerMethodArgumentResolverHandler>nativeHandler()
                    .get(context);
            if (value != null) {
                return value;
            }
        }

        // 清除缓存
        providerSupport.remove();
        throw new IllegalArgumentException("can't resolve parameter of Param type,current param type is " + parameter.getParameterType() + " !!!");
    }

    @SafeVarargs
    public final FactoryBasedHandlerMethodArgumentResolver addArgumentResolverHandlers(HandlerMethodArgumentResolverHandlerProvider<? extends Handler>... providers) {
        handlerFactory.registerHandlers(Arrays.asList(providers));
        return this;
    }
}
