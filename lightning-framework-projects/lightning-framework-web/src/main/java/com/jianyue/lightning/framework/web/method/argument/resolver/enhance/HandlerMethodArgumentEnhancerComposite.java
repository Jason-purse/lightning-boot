package com.jianyue.lightning.framework.web.method.argument.resolver.enhance;

import com.jianyue.lightning.framework.web.method.argument.context.MethodArgumentContext;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.MethodParameter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerMethodArgumentEnhancerComposite implements HandlerMethodArgumentEnhancer {
    private final List<HandlerMethodArgumentEnhancer> argumentEnhancers = new ArrayList<>();
    private final Map<MethodParameter, List<HandlerMethodArgumentEnhancer>> argumentResolverCache = new ConcurrentHashMap<>(256);

    public HandlerMethodArgumentEnhancerComposite() {
    }

    public HandlerMethodArgumentEnhancerComposite addResolver(HandlerMethodArgumentEnhancer resolver) {
        this.argumentEnhancers.add(resolver);
        return this;
    }

    public HandlerMethodArgumentEnhancerComposite addEnhancers(@Nullable HandlerMethodArgumentEnhancer... resolvers) {
        if (resolvers != null) {
            Collections.addAll(this.argumentEnhancers, resolvers);
        }

        return this;
    }

    public HandlerMethodArgumentEnhancerComposite addEnhancers(@Nullable List<? extends HandlerMethodArgumentEnhancer> resolvers) {
        if (resolvers != null) {
            this.argumentEnhancers.addAll(resolvers);
        }

        return this;
    }

    public List<HandlerMethodArgumentEnhancer> getResolvers() {
        return Collections.unmodifiableList(this.argumentEnhancers);
    }

    public void clear() {
        this.argumentEnhancers.clear();
        this.argumentResolverCache.clear();
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return this.getArgumentEnhancers(parameter) != null;
    }

    @Override
    public void enhanceArgument(MethodArgumentContext methodArgumentContext) {
        List<HandlerMethodArgumentEnhancer> resolvers = this.getArgumentEnhancers(methodArgumentContext.getMethodParameter());
        if (resolvers == null) {
            throw new IllegalArgumentException("Unsupported parameter type [" + methodArgumentContext.getMethodParameter().getParameterType().getName() + "]. supportsParameter should be called first.");
        } else {
            for (HandlerMethodArgumentEnhancer resolver : resolvers) {
                resolver.enhanceArgument(methodArgumentContext);
            }
        }
    }

    @Nullable
    private List<HandlerMethodArgumentEnhancer> getArgumentEnhancers(MethodParameter parameter) {
        List<HandlerMethodArgumentEnhancer> result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            Iterator<HandlerMethodArgumentEnhancer> var3 = this.argumentEnhancers.iterator();

            List<HandlerMethodArgumentEnhancer> argumentEnhancers = new ArrayList<>();
            while (var3.hasNext()) {
                HandlerMethodArgumentEnhancer resolver = var3.next();
                if (resolver.supportsParameter(parameter)) {
                    argumentEnhancers.add(resolver);
                }
            }

            this.argumentResolverCache.put(parameter, argumentEnhancers);
            result = argumentEnhancers;
        }

        return result;
    }
}
