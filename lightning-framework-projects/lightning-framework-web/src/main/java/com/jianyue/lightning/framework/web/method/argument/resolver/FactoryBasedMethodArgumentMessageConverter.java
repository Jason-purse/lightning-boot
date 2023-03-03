package com.jianyue.lightning.framework.web.method.argument.resolver;

import com.jianyue.lightning.boot.starter.util.factory.HandlerFactory;
import com.jianyue.lightning.boot.starter.util.factory.HandlerProvider;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

/**
 * @author FLJ
 * @date 2023/3/3
 * @time 16:46
 * @Description 解决 request body中, 想注入其他数据信息的问题 !!!
 * <p>
 * 根据请求头中 进行解析 / 或者 注入用户信息 !!!
 */
public class FactoryBasedMethodArgumentMessageConverter extends MappingJackson2HttpMessageConverter {

    private final HandlerFactory handlerFactory = new HandlerFactory();


    @NotNull
    @SneakyThrows
    @Override
    protected   Object readInternal(@NotNull Class<?> clazz, @NotNull HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Object target = super.readInternal(clazz, inputMessage);
        return getObject(clazz, inputMessage, target);
    }

    private Object getObject(@NotNull Class<?> clazz, @NotNull HttpInputMessage inputMessage, @Nullable Object target) throws Exception {
        List<HandlerProvider> handlers = handlerFactory.getRequiredHandlers(clazz, inputMessage);
        for (HandlerProvider handler : handlers) {
            target = handler.getHandler()
                    .<JsonHttpMessageMethodArgumentResolver>nativeHandler()
                    .get(new HttpMessageContext(clazz, target, inputMessage));
        }
        // 最后传递出去 target !!!
        return target;
    }

    public Object readForDirectInvoke(@Nullable Object target, Class<?> clazz, @NotNull HttpInputMessage inputMessage) throws Exception {
        if(target == null) {
            Constructor<?> ctor = BeanUtils.getResolvableConstructor(clazz);
            target = ctor.newInstance();
        }
        return getObject(clazz, inputMessage, target);
    }

    public void registerHandlers(JsonHttpMessageMethodArgumentResolverProvider... providers) {
        handlerFactory.registerHandlers(Arrays.asList(providers));
    }
}
