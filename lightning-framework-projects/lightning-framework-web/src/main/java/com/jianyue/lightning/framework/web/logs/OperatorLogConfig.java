//package com.jianyue.lightning.boot.starter.web.logs;
//
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
//
///**
// * @author Liupan
// * @date 2021/11/13 17:49
// */
//@Configuration
//@AutoConfigureBefore(WebMvcAutoConfiguration.class)
//public class OperatorLogConfig implements WebMvcConfigurer , DisposableBean {
//
//    @Autowired
//    KafkaTemplate<String, Object> kafkaTemplate;
//
//
//    @Autowired
//    private ThreadPoolTaskExecutor executor;
//
//
//    private OperatorLogInterceptor interceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        interceptor = new OperatorLogInterceptor(kafkaTemplate, executor);
//        registry.addInterceptor(interceptor);
//    }
//
//
//    /*@Primary
//    @Bean
//    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter(Jackson2ObjectMapperBuilder builder) {
//        RequestMappingHandlerAdapter requestMappingHandlerAdapter = new ProfilerRequestMappingHandlerAdapter();
//        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        final ObjectMapper objectMapper = builder.build();
//        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
//        // 传递给 @RequestBody 的 参数处理器,否则无法解析消息
//        requestMappingHandlerAdapter.getMessageConverters()
//                .add(mappingJackson2HttpMessageConverter);
//
//        return requestMappingHandlerAdapter;
//    }*/
//
//
//    @Bean
//    public WebMvcRegistrations webMvcRegistrations() {
//        return new WebMvcRegistrations() {
//            @Override
//            public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
//                return new ProfilerRequestMappingHandlerAdapter();
//            }
//        };
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        if(interceptor != null) {
//            interceptor.close();
//        }
//    }
//}
