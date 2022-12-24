package com.jianyue.lightning.framework.design.pattern.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author FLJ
 * @dateTime 2022/1/25 11:25
 * @description 动态代理策略执行器...
 */
public class DefaultStrategyServiceInvocationHandler implements InvocationHandler {

    private final AbstractStrategyService target;


    DefaultStrategyServiceInvocationHandler(AbstractStrategyService target) {
        this.target = target;
    }

    public AbstractStrategyService getTarget() {
        return target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            boolean byType = method.getName().endsWith("ByType");
            if (byType) {
                Object o = this.target.acquireTarget(args[0].toString());
                return method.invoke(o, args);
            }

            return method.invoke(this.target, args);
        } catch (Exception e) {
            throw e;
        }
    }
}
