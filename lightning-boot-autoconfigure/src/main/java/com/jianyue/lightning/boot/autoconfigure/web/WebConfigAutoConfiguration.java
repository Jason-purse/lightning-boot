package com.jianyue.lightning.boot.autoconfigure.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jianyue.lightning.boot.exception.feign.AbstractFeignApplicationException;
import com.jianyue.lightning.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import com.jianyue.lightning.framework.web.advice.DefaultGlobalControllerExceptionHandler;
import com.jianyue.lightning.framework.web.logs.LightningWebLogInterceptor;
import com.jianyue.lightning.framework.web.logs.ProfilerRequestMappingHandlerAdapter;

import java.util.List;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 12:02
 * @Description Web 自动配置
 */
@Configuration
@EnableConfigurationProperties(WebProperties.class)
public class WebConfigAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private WebProperties webProperties;

    /**
     * 提供了拦截器 ... 决定是否启用 ..
     * 确保则不加入 ..
     */
    @Bean
    @ConditionalOnProperty(value = "lightning.web.config.logging.enable")
    public WebMvcRegistrations webMvcRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
                return new ProfilerRequestMappingHandlerAdapter();
            }
        };
    }

    /**
     *  确保加入了 ProfilerRequestMappingHandlerAdapter 才进行 LightningWebLogInterceptor拦截器获取
     */
    @ConditionalOnProperty(value = "lightning.web.config.logging.enable")
    @Bean
    public void addInterceptors(InterceptorRegistry registry, LightningWebLogInterceptor logInterceptor) {
        registry.addInterceptor(logInterceptor);
    }



    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        if(webProperties.getJson().getSerializeIncludeNonNull()) {
            converters.stream().filter(ele -> ele instanceof MappingJackson2HttpMessageConverter)
                    .forEach(ele -> {
                        ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) ele).getObjectMapper();
                        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    });
        }
    }

    /**
     * feign 和  普通异常的拦截自动切换 ...
     */
    @Configuration
    @ConditionalOnClass(DefaultGlobalControllerExceptionHandler.class)
    public static class WebExceptionHandlerAutoConfiguration {


        @Configuration
        @ConditionalOnClass(AbstractFeignApplicationException.class)
        @RestControllerAdvice
        public static class DefaultGlobalExceptionWithFeignControllerExceptionHandler extends DefaultGlobalControllerExceptionHandler {

            /**
             * feign 异常能够处理 调用接口产生的feign 异常 ..
             *
             * @param e e
             * @return Result ...
             */
            @ExceptionHandler(AbstractFeignApplicationException.class)
            public Result<?> handleFeignException(AbstractFeignApplicationException e) {
                return super.handleRootException(e);
            }

        }

        /**
         * 非feign 系统的调用接口产生的 application exception
         */
        @Configuration
        @ConditionalOnMissingClass("com.jianyue.lightning.boot.exception.feign.AbstractFeignApplicationException")
        @RestControllerAdvice
        public static class DefaultGlobalExceptionControllerExceptionHandler extends DefaultGlobalControllerExceptionHandler {

        }
    }


}
