package com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver;

import com.fasterxml.jackson.databind.JavaType;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.ControllerSupport;
import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple;
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
import com.jianyue.lightning.util.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.Predicate;

public class SimpleForGenericCrudHandlerMethodArgumentResolverHandler implements HandlerMethodArgumentResolverHandler {
    private final Predicate<MethodParameter> predicate =
            methodParameter -> AbstractGenericController.class.isAssignableFrom(methodParameter.getDeclaringClass())
                    && ControllerSupport.Companion.getParamClassState().get() != null
                    && Param.class.isAssignableFrom(methodParameter.getParameterType());

    private final GenericCRUDModelAttributeMethodProcessor genericCrudMethodProcessor = new GenericCRUDModelAttributeMethodProcessor();


    private final MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(Jackson2ObjectMapperBuilder.json().build()) {
        @NotNull
        @Override
        protected JavaType getJavaType(@NotNull Type type, Class<?> contextClass) {
            Tuple<Class<?>, Object> tuple = ControllerSupport.Companion.getParamClassState().get();
            if (tuple != null) {
                if (type instanceof Class<?> typeClass && Param.class.isAssignableFrom(typeClass)) {
                    return getObjectMapper().getTypeFactory().constructType(tuple.getFirst());
                }
            }
            return super.getJavaType(type, contextClass);
        }

        @NotNull
        @Override
        @SuppressWarnings("unchecked")
        public Object read(@NotNull Type type, Class<?> contextClass, @NotNull HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            Tuple<Class<?>, Object> tuple = ControllerSupport.Companion.getParamClassState().get();
            Object read = super.read(type, contextClass, inputMessage);
            if (tuple != null) {
                ControllerSupport.Companion.getParamClassState().set(((Tuple<Class<?>, Object>) tuple.getSecond()));
            }
            return read;
        }
    };

    /**
     * 支持特定的序列化类型 ,例如添加序列化器或者反序列化器 ..
     */
    private final JsonUtil jsonUtil = JsonUtil.withDefaultDateOfChina();


    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public MappingJackson2HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public Predicate<MethodParameter> getPredicate() {
        return predicate;
    }

    @Override
    public Object get(MethodArgumentContext value) {
        // 这种情况下,直接处理即可 ...
        try {
            // 判断有没有使用@RequestBody
            // 如果没有,则表示从参数中获取 ..
            return genericCrudMethodProcessor.resolveArgument(
                    value.getMethodParameter(),
                    value.getMavContainer(),
                    value.getRequest(),
                    value.getBinderFactory()
            );

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}

