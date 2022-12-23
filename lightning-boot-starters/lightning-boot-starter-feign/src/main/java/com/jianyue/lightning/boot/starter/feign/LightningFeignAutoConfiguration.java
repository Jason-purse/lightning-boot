package com.jianyue.lightning.boot.starter.feign;

import org.springframework.context.annotation.Bean;
/**
 * @author FLJ
 * @date 2022/12/20
 * @time 13:51
 * @Description feign 切面处理 ..
 */
public class LightningFeignAutoConfiguration {

    @Bean
    public FeignExceptionAspect feignExceptionAspect() {
        return new FeignExceptionAspect();
    }
}
