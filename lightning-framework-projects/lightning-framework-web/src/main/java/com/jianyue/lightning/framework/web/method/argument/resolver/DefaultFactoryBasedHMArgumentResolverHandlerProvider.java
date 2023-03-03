package com.jianyue.lightning.framework.web.method.argument.resolver;

import com.jianyue.lightning.boot.starter.util.factory.Handler;
import com.jianyue.lightning.boot.starter.util.factory.HandlerProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;

import java.util.function.Predicate;
/**
 * @author FLJ
 * @date 2023/2/23
 * @time 10:38
 * @Description 为基于工厂的HandlerMethodArgumentResolver 提供解析器处理器
 *
 * 提供的对象是 {@link FactoryBasedHMArgumentResolverHandler} ...
 *
 * 这个Provider 可以基于Key 拥有相同key的兄弟 {@link DefaultFactoryBasedHMArgumentResolverHandlerProvider}
 * 同时基于不同的{@link #predicate} 来实现不同策略实现不同的handler 提供 !!!
 */
public class DefaultFactoryBasedHMArgumentResolverHandlerProvider<H extends Handler> implements FactoryBasedHMArgumentResolverHandlerProvider {

    private final Object key;

    private final H handler;

    private final Predicate<MethodParameter> predicate;

    public DefaultFactoryBasedHMArgumentResolverHandlerProvider(Object key, H handler, Predicate<MethodParameter> predicate) {
        this.key = key;
        this.handler = handler;
        this.predicate = predicate;
    }

    @Override
    public Object key() {
        return key;
    }

    @NotNull
    @Override
    public H getHandler() {
        return handler;
    }

    @Override
    public boolean support(Object predicate) {
        return this.predicate.test(((MethodParameter) predicate));
    }
}
