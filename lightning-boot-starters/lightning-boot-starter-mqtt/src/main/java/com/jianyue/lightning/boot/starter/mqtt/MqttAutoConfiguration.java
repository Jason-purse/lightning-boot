package com.jianyue.lightning.boot.starter.mqtt;

import com.jianyue.lightning.boot.starter.mqtt.v3.MqttV3AutoConfiguration;
import com.jianyue.lightning.boot.starter.mqtt.v5.MqttV5AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.integration.annotation.IntegrationComponentScan;


/**
 * @author FLJ
 * @dateTime 2022/1/2 15:24
 * @description mqtt v1.3.x 开始的 自动配置..
 *
 * @update mqtt v1.5.0 更新 ...
 *
 * @see MqttV5AutoConfiguration
 * @see MqttV3AutoConfiguration
 * @see MqttListenerConfiguration
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
@IntegrationComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = MqttTemplate.class))
public class MqttAutoConfiguration {

}
