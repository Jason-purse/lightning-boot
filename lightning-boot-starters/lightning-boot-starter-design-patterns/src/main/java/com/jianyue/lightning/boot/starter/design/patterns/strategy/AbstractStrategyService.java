package com.jianyue.lightning.boot.starter.design.patterns.strategy;


import com.jianyue.lightning.exception.DefaultApplicationException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author FLJ
 * @dateTime 2022/1/25 10:10
 * @description 策略服务抽象类..
 */
class AbstractStrategyService implements StrategyService<Object> {

    private final Class<?> tClass;

    private final Map<String, StrategyService<?>> cache = new ConcurrentHashMap<>(4);

    protected AbstractStrategyService(@NotNull Class<?> tClass) {
        this.tClass = tClass;
    }

    @Override
    public String getType() {
        return String.format("%s-Strategy-proxy", tClass.getSimpleName());
    }

    @Override
    public Class<?> getTargetClass() {
        return tClass;
    }


    public void addStrategyService(Object tStrategyService) {
        StrategyService<?> tStrategyService1 = (StrategyService<?>) tStrategyService;
        cache.compute(tStrategyService1.getType(), (key, strategyService) -> {
           if(strategyService == null) {
               return tStrategyService1;
           }
            throw DefaultApplicationException.of(String.format("当前策略服务存在多个相同策略模式,相同策略模式服务: type: %s, service: [%s]", key,strategyService));
        });
    }

    /**
     * 获取执行目标
     *
     * @param type 策略
     * @return 策略目标
     */
    Object acquireTarget(String type) {
        StrategyService<?> t = cache.get(type);
        if (t == null) {
            throw DefaultApplicationException.of(String.format("当前服务不存在对应策略为: %s的实现,目标类为: %s", type, tClass));
        }
        return t;
    }

}
