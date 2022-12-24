package com.jianyue.lightning.boot.autoconfigure.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lightning.web.config")
public class WebProperties {

    private LoggingConfig logging = new LoggingConfig();

    /**
     * 控制jackson 行为
     */
    private JacksonConfig json = new JacksonConfig();


    @Data
    public static class LoggingConfig {

        private Boolean enable = false;
    }


    @Data
    public static class JacksonConfig {

        /**
         * 序列化只包括非空 ...
         */
        private Boolean serializeIncludeNonNull = false;
    }
}
