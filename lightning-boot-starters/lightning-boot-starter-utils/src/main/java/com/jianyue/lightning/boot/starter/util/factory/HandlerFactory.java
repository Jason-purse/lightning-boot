package com.jianyue.lightning.boot.starter.util.factory;

import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author FLJ
 * @date 2023/1/9
 * @time 12:44
 * @Description Handler 工厂...
 * <p>
 * 实现基于工厂形式的 对应bean 获取 ..
 * 并不保证 bean的单例性,取决于 provider的处理形式 以及 handler的扩展性 ..
 */
public class HandlerFactory {

    private final ConcurrentHashMap<Object, List<HandlerProvider>> handlerCache = new ConcurrentHashMap<>();

    public  <T extends HandlerProvider> void registerHandler(T handlerProvider) {
        handlerCache
                .computeIfAbsent(handlerProvider.key(), key -> new LinkedList<>())
                .add(handlerProvider);
    }

    public  <T extends HandlerProvider> void registerHandlers(List<T> handlerProviders) {
        for (T handlerProvider : handlerProviders) {
            handlerCache
                    .computeIfAbsent(handlerProvider.key(), key -> new LinkedList<>())
                    .add(handlerProvider);
        }
    }


    @Nullable
    public  List<HandlerProvider> getHandlers(Object key) {
        return handlerCache.get(key);
    }

    public List<HandlerProvider> getRequiredHandlers(Object key,Object predicate) {

        List<HandlerProvider> handlers = getHandlers(key);
        if(handlers != null && handlers.size() >  0) {
            return handlers.stream().filter(ele -> ele.support(predicate)).toList();
        }
        return Collections.emptyList();
    }


    @Nullable
    public  HandlerProvider getHandler(Object key, Object predicate) {
        List<HandlerProvider> handlers = getHandlers(key);
        if (handlers != null) {
            for (HandlerProvider handler : handlers) {
                if (handler.support(predicate)) {
                    return handler;
                }
            }
        }
        return null;
    }

    public  HandlerProvider getRequiredHandler(Object key, Object predicate) {
        HandlerProvider handler = getHandler(key, predicate);
        Assert.notNull(handler, "can't found an handler for " + key + "!!!");
        return handler;
    }
}
