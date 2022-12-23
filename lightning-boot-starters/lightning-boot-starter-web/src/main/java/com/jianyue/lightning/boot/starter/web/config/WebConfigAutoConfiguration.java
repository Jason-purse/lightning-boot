package com.jianyue.lightning.boot.starter.web.config;

import com.jianyue.lightning.boot.starter.exception.feign.AbstractFeignApplicationException;
import com.jianyue.lightning.boot.starter.web.advice.controller.DefaultGlobalControllerExceptionHandler;
import com.jianyue.lightning.boot.starter.web.logs.LightningWebLogInterceptor;
import com.jianyue.lightning.boot.starter.web.logs.ProfilerRequestMappingHandlerAdapter;
import com.jianyue.lightning.result.Result;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 12:02
 * @Description Web 自动配置
 */
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(LightningWebConfigProperties.class)
public class WebConfigAutoConfiguration implements WebMvcConfigurer {

    /**
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

    @Bean
    public void addInterceptors(InterceptorRegistry registry, LightningWebLogInterceptor logInterceptor) {
        registry.addInterceptor(logInterceptor);
    }

    @Configuration
    public static class WebExceptionHandlerAutoConfiguration {


        @Configuration
        @ConditionalOnClass(AbstractFeignApplicationException.class)
        @RestControllerAdvice
        public static class DefaultGlobalExceptionWithFeignControllerExceptionHandler extends DefaultGlobalControllerExceptionHandler {

            /**
             * feign 异常能够处理 调用接口产生的feign 异常 ..
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
        @ConditionalOnMissingClass("com.jianyue.lightning.boot.starter.exception.feign.AbstractFeignApplicationException")
        @RestControllerAdvice
        public static class DefaultGlobalExceptionControllerExceptionHandler extends DefaultGlobalControllerExceptionHandler {

        }
    }



}
