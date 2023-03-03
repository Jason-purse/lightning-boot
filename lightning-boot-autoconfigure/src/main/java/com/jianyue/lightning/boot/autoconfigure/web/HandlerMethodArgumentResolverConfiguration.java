package com.jianyue.lightning.boot.autoconfigure.web;

import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerFactoryConfigurer;
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerMethodArgumentResolver;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author FLJ
 * @date 2023/3/2
 * @time 12:03
 * @Description 基于工厂的方法参数解析器配置 !!!
 */
@AutoConfiguration
@Configuration
public class HandlerMethodArgumentResolverConfiguration implements WebMvcConfigurer {
    private final FactoryBasedHandlerMethodArgumentResolver factoryBasedHandlerMethodArgumentResolver = new FactoryBasedHandlerMethodArgumentResolver();

    @Autowired(required = false)
    public void setFactoryBasedHandlerMethodArgumentResolvers(FactoryBasedHandlerFactoryConfigurer... configurers) {
        for (FactoryBasedHandlerFactoryConfigurer configurer : configurers) {
            configurer.configMethodArgumentResolver(factoryBasedHandlerMethodArgumentResolver);
        }
    }

    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(factoryBasedHandlerMethodArgumentResolver);
    }
}
