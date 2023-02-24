package com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver;

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
 */
public class HandlerMethodArgumentResolverHandlerProvider<H extends Handler> implements HandlerProvider {

    private final Object key;

    private final H handler;

    private final Predicate<MethodParameter> predicate;

    public HandlerMethodArgumentResolverHandlerProvider(Object key, H handler, Predicate<MethodParameter> predicate) {
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
    public Handler getHandler() {
        return handler;
    }

    @Override
    public boolean support(Object predicate) {
        return this.predicate.test(((MethodParameter) predicate));
    }
}
