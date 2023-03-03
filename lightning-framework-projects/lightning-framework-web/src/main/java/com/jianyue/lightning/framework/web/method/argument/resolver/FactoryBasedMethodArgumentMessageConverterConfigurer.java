package com.jianyue.lightning.framework.web.method.argument.resolver;
/**
 * @author FLJ
 * @date 2023/3/3
 * @time 17:35
 * @Description 基于工厂的方法消息转换器配置器
 */
public interface FactoryBasedMethodArgumentMessageConverterConfigurer {

    void configure(FactoryBasedMethodArgumentMessageConverter messageConverter);
}
