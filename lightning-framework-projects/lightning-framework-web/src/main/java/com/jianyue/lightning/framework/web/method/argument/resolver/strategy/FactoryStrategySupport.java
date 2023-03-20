package com.jianyue.lightning.framework.web.method.argument.resolver.strategy;

import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;

import java.util.Arrays;
import java.util.List;

public class FactoryStrategySupport {

   private static final HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();

    public void addResolvers(HandlerMethodArgumentFactoryStrategyResolver... resolvers) {
        addResolvers(Arrays.asList(resolvers));
    }

    public void addResolvers(List<HandlerMethodArgumentFactoryStrategyResolver> resolvers) {
        if (resolvers != null) {
            FactoryStrategySupport.resolvers.addResolvers(resolvers);
        }
    }

    static HandlerMethodArgumentResolverComposite getResolvers() {
        return resolvers;
    }
}
