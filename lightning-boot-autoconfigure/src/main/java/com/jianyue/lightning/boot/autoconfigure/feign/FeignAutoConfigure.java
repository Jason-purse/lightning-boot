package com.jianyue.lightning.boot.autoconfigure.feign;

import com.jianyue.lightning.boot.starter.feign.LightningFeignAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 14:38
 * @Description  feign auto configuration
 */
@ConditionalOnClass(LightningFeignAutoConfiguration.class)
@Import(LightningFeignAutoConfiguration.class)
public class FeignAutoConfigure {

}
