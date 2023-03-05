package com.jianyue.lightning.framework.web.config;

import com.jianyue.lightning.framework.web.logs.DataUtil;
import com.jianyue.lightning.framework.web.method.argument.resolver.enhance.EnhanceHandlerMethodArgumentResolver;
import com.jianyue.lightning.framework.web.method.argument.resolver.enhance.HandlerMethodArgumentEnhancerComposite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
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

    @Nullable
    private HandlerMethodArgumentEnhancerComposite enhancers;

    private HandlerMethodArgumentResolverComposite firstClassSupportResolvers;


    @NotNull
    @Override
    protected Object[] getMethodArgumentValues(@NotNull NativeWebRequest request, ModelAndViewContainer mavContainer, @NotNull Object... providedArgs) throws Exception {
        Object[] methodArgumentValues = super.getMethodArgumentValues(request, mavContainer, providedArgs);
        DataUtil.set(methodArgumentValues);
        return methodArgumentValues;
    }

    @Override
    public void setHandlerMethodArgumentResolvers(@NotNull HandlerMethodArgumentResolverComposite argumentResolvers) {
        EnhanceHandlerMethodArgumentResolver argumentResolver = new EnhanceHandlerMethodArgumentResolver();
        if(firstClassSupportResolvers != null) {
            argumentResolver.addHandlerMethodArgumentResolvers(firstClassSupportResolvers);
        }
        argumentResolver.addHandlerMethodArgumentResolvers(argumentResolvers);

        if(enhancers != null) {
            argumentResolver.addHandlerMethodArgumentEnhancers(enhancers);
        }
        super.setHandlerMethodArgumentResolvers(new HandlerMethodArgumentResolverComposite().addResolver(argumentResolver));
    }

    public void setEnhancers(@NotNull HandlerMethodArgumentEnhancerComposite enhancers) {
        this.enhancers = enhancers;
    }

    public void setFirstClassSupportResolvers(HandlerMethodArgumentResolverComposite firstClassSupportResolvers) {
        this.firstClassSupportResolvers = firstClassSupportResolvers;
    }
}
