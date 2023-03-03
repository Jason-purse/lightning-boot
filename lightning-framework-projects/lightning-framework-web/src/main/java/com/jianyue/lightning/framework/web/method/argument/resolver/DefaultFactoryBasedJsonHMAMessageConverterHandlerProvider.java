package com.jianyue.lightning.framework.web.method.argument.resolver;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;

import java.util.function.Predicate;

/**
 * @author FLJ
 * @date 2023/3/2
 * @time 12:19
 * @Description  默认实现HandlerMethodArgumentResolver 而不是一个匿名类 !!
 */
@AllArgsConstructor
public class DefaultFactoryBasedJsonHMAMessageConverterHandlerProvider implements FactoryBasedJsonHMAMessageConverterHandlerProvider {
    private Object key;
    private Predicate<MethodParameter> predicate;
    private FactoryBasedJsonHMAMessageConverterHandler handler;

    @Override
    public Object key() {
        return key;
    }

    @Override
    public boolean support(Object value) {
        return predicate.test(((MethodParameter) value));
    }

    @NotNull
    @Override
    public FactoryBasedJsonHMAMessageConverterHandler getHandler() {
        return handler;
    }
}
