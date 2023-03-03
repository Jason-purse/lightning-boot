package com.jianyue.lightning.framework.web.method.argument.resolver;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpInputMessage;

@Data
@AllArgsConstructor
public class HttpMessageContext {
    @NotNull
    private Class<?> targetClass;

    @Nullable
    private Object target;

    @NotNull
    private HttpInputMessage inputMessage;
}
