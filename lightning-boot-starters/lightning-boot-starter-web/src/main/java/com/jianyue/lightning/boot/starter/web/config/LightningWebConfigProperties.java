package com.jianyue.lightning.boot.starter.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 12:01
 * @Description Web 配置属性
 */
@Data
@ConfigurationProperties(prefix = "lightning.web.config")
public class LightningWebConfigProperties {

    private LoggingConfig logging;


    @Data
    public static class LoggingConfig {

        private Boolean enable;
    }

}
