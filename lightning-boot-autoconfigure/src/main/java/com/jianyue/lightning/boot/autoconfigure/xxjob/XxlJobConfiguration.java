package com.jianyue.lightning.boot.autoconfigure.xxjob;

import com.jianyue.lightning.framework.xxjob.AbstractXxlJobConfiguration;
import com.jianyue.lightning.framework.xxjob.XxlJobProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnClass(XxlJobProperties.class)
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobConfiguration extends AbstractXxlJobConfiguration {

    @Profile(value = "docker")
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor(final InetUtils inetUtils) {
        logger.info("XxlJob executor is starting from docker ...");
        InetUtils.HostInfo firstNonLoopbackHostInfo = inetUtils.findFirstNonLoopbackHostInfo();
        String ipAddress = firstNonLoopbackHostInfo.getIpAddress();
        logger.info("XxlJob executor ip is {}", ipAddress);
        return createXxlJobExecutor(ipAddress);
    }

    @Bean(initMethod = "start", destroyMethod = "destroy")
    @ConditionalOnMissingBean(XxlJobConfiguration.class)
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info("Xxljob is starting from not docker env");
        return createXxlJobExecutor(null);
    }
}
