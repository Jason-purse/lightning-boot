package com.jianyue.lightning.framework.web.config;

import com.jianyue.lightning.framework.web.advice.MethodParameterForHttpRequestBodyAdvice;
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedMethodArgumentMessageConverterConfigurer;
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerMethodArgumentResolverConfigurer;
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerMethodArgumentResolver;
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedMethodArgumentMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author FLJ
 * @date 2023/3/3
 * @time 17:59
 * @Description 方法参数解析配置
 */
@Configuration
public class MethodArgumentResolverConfiguration implements WebMvcConfigurer {
    private final static FactoryBasedMethodArgumentMessageConverter factoryBasedMethodArgumentMessageConverter = new FactoryBasedMethodArgumentMessageConverter();
    private final static FactoryBasedHandlerMethodArgumentResolver factoryBasedMethodArgumentArgumentResolver = new FactoryBasedHandlerMethodArgumentResolver();


    @Autowired(required = false)
    public void configure(FactoryBasedMethodArgumentMessageConverterConfigurer... configurers) {
        for (FactoryBasedMethodArgumentMessageConverterConfigurer configurer : configurers) {
            configurer.configure(factoryBasedMethodArgumentMessageConverter);
        }
    }

    @Autowired(required = false)
    public void configureBasedFactoryMethodArgumentResolvers(List<FactoryBasedHandlerMethodArgumentResolverConfigurer> configurers) {
        for (FactoryBasedHandlerMethodArgumentResolverConfigurer configurer : configurers) {
            configurer.configMethodArgumentResolver(factoryBasedMethodArgumentArgumentResolver);
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(factoryBasedMethodArgumentArgumentResolver);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(factoryBasedMethodArgumentMessageConverter);
    }


    /**
     * 如果非rest controller 失效,可能还需要处理 !!
     */
    @RestControllerAdvice
    @Configuration
    public static class  MethodParameterForHttpRequestBodyAdviceImpl extends MethodParameterForHttpRequestBodyAdvice {

        public MethodParameterForHttpRequestBodyAdviceImpl() {
            super(factoryBasedMethodArgumentMessageConverter);
        }
    }
}
