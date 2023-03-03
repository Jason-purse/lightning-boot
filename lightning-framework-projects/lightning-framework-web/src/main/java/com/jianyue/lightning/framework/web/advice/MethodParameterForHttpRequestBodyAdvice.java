package com.jianyue.lightning.framework.web.advice;

import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedJsonHMAMessageConverterHandler;
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedMethodArgumentMessageConverter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author FLJ
 * @date 2023/3/3
 * @time 17:18
 * @Description 用来为了对requestBody的结果进行解析 !!!
 * 为空的时候,无法进行解析 或者进一步让 {@link FactoryBasedJsonHMAMessageConverterHandler} 配置 !!!
 */
public class MethodParameterForHttpRequestBodyAdvice extends RequestBodyAdviceAdapter {
    public static final String DEFAULT_HTTP_REQUEST_BODY_METHOD_PARAMETER_KEY = "lightning.security.method.argument.message.body.resolver";

    private final FactoryBasedMethodArgumentMessageConverter messageConverter;

    public MethodParameterForHttpRequestBodyAdvice(FactoryBasedMethodArgumentMessageConverter converter) {
        this.messageConverter = converter;
    }

    @Override
    public boolean supports(@NotNull MethodParameter methodParameter, @NotNull Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @NotNull
    @Override
    public HttpInputMessage beforeBodyRead(@NotNull HttpInputMessage inputMessage, @NotNull MethodParameter parameter, @NotNull Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        ServletServerHttpRequest message = (ServletServerHttpRequest) inputMessage;
        // 当前方法参数 !!!
        message.getServletRequest().setAttribute(DEFAULT_HTTP_REQUEST_BODY_METHOD_PARAMETER_KEY, parameter);
        // 设置属性
        return super.beforeBodyRead(inputMessage, parameter, targetType, converterType);
    }

    @SneakyThrows
    @Override
    public Object handleEmptyBody(Object body, @NotNull HttpInputMessage inputMessage, MethodParameter parameter, @NotNull Type targetType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        Object o = this.messageConverter.readForDirectInvoke(null, parameter.getParameterType(), parameter,inputMessage);
        if (o != null) {
            return o;
        }
        // 进行解析 !!!
        return super.handleEmptyBody(body, inputMessage, parameter, targetType, converterType);
    }
}
