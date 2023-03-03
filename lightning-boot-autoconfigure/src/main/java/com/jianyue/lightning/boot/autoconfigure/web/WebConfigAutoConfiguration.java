package com.jianyue.lightning.boot.autoconfigure.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jianyue.lightning.boot.exception.feign.AbstractFeignApplicationException;
import com.jianyue.lightning.framework.web.config.LightningWebConfigurations;
import com.jianyue.lightning.result.Result;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.jianyue.lightning.framework.web.advice.DefaultGlobalControllerExceptionHandler;

import java.util.List;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 12:02
 * @Description Web 自动配置
 */
@Configuration
@EnableConfigurationProperties(WebProperties.class)
@RequiredArgsConstructor
@Import(LightningWebConfigurations.class)
public class WebConfigAutoConfiguration implements WebMvcConfigurer {

    private final WebProperties webProperties;

    @Override
    public void extendMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.extendMessageConverters(converters);
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
