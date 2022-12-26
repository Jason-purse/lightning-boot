package com.jianyue.lightning.framework.xxjob;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

//    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

//    @Value("${xxl.job.accessToken}")
    private String accessToken;

//    @Value("${xxl.job.executor.appname}")
    private String appname;

//    @Value("${xxl.job.executor.address}")
    private String address;

//    @Value("${xxl.job.executor.ip}")
    private String ip;

//    @Value("${xxl.job.executor.port}")
    private int port;

//    @Value("${xxl.job.executor.logpath}")
    private String logPath;

//    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;
}
