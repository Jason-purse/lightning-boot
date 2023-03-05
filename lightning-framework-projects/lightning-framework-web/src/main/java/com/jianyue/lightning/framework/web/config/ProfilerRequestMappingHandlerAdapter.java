package com.jianyue.lightning.framework.web.config;

import com.jianyue.lightning.framework.web.method.argument.resolver.FirstClassSupportHandlerMethodArgumentResolver;
import com.jianyue.lightning.framework.web.method.argument.resolver.enhance.HandlerMethodArgumentEnhancer;
import com.jianyue.lightning.framework.web.method.argument.resolver.enhance.HandlerMethodArgumentEnhancerComposite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.util.List;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 12:05
 * @Description 包含有拦截方法前后的一个requestMappingHandlerAdapter ..
 */
public class ProfilerRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {
    @Nullable
    private HandlerMethodArgumentEnhancerComposite enhancers;

    @Nullable
    private HandlerMethodArgumentResolverComposite firstClassSupportHandlerMethodArgumentResolvers;
    public void setEnhancers(List<HandlerMethodArgumentEnhancer> enhancers) {
        if (enhancers == null) {
            this.enhancers = null;
        } else {
            this.enhancers = new HandlerMethodArgumentEnhancerComposite();
            this.enhancers.addEnhancers(enhancers);
        }
    }


    public void setFirstClassSupportHandlerMethodArgumentResolvers(List<FirstClassSupportHandlerMethodArgumentResolver> firstClassSupportHandlerMethodArgumentResolvers) {
        if (firstClassSupportHandlerMethodArgumentResolvers == null) {
            this.firstClassSupportHandlerMethodArgumentResolvers = null;
        } else {
            this.firstClassSupportHandlerMethodArgumentResolvers = new HandlerMethodArgumentResolverComposite();
            this.firstClassSupportHandlerMethodArgumentResolvers.addResolvers(firstClassSupportHandlerMethodArgumentResolvers);
        }
    }

    @NotNull
    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(@NotNull HandlerMethod handlerMethod) {
        ProfilerServletInvocableHandlerMethod invocableHandlerMethod = new ProfilerServletInvocableHandlerMethod(handlerMethod);
        if(enhancers != null) {
            invocableHandlerMethod.setEnhancers(enhancers);
        }

        if(firstClassSupportHandlerMethodArgumentResolvers != null) {
            invocableHandlerMethod.setFirstClassSupportResolvers(firstClassSupportHandlerMethodArgumentResolvers);
        }

        return invocableHandlerMethod;
    }

}
