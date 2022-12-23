package com.jianyue.lightning.boot.starter.web.logs;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
/**
 * @author FLJ
 * @date 2022/12/20
 * @time 12:05
 * @Description 包含有拦截方法前后的一个requestMappingHandlerAdapter ..
 */
public class ProfilerRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {
    @NotNull
    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(@NotNull HandlerMethod handlerMethod) {
        return new ProfilerServletInvocableHandlerMethod(handlerMethod);
    }
}
