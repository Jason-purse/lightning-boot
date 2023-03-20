package com.jianyue.lightning.framework.web.method.argument.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;

@Data
@AllArgsConstructor
@Builder
public class HttpMessageContext {
    @NotNull
    private Class<?> targetClass;

    @NotNull
    private MethodParameter methodParameter;

    @Nullable
    private Object target;


    @NotNull
    private HttpInputMessage inputMessage;
}
