package com.jianyue.lightning.framework.web.logs;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * @author FLJ
 * @date 2021/11/19 11:17
 * @description 获取参数值的profile invocable method
 */
public class ProfilerServletInvocableHandlerMethod extends ServletInvocableHandlerMethod {
    public ProfilerServletInvocableHandlerMethod(HandlerMethod method) {
        super(method);
    }
    @NotNull
    @Override
    protected Object[] getMethodArgumentValues(@NotNull NativeWebRequest request, ModelAndViewContainer mavContainer, @NotNull Object... providedArgs) throws Exception {
        Object[] methodArgumentValues = super.getMethodArgumentValues(request, mavContainer, providedArgs);
        DataUtil.set(methodArgumentValues);
        return methodArgumentValues;
    }
}
