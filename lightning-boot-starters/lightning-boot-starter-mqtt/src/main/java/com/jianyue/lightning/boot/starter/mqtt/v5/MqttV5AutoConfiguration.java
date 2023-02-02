package com.jianyue.lightning.boot.starter.mqtt.v5;


import com.jianyue.lightning.boot.starter.mqtt.MqttProperties;
import com.jianyue.lightning.boot.starter.mqtt.v3.MqttV3AutoConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaderMapper;
import org.springframework.messaging.converter.ByteArrayMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author FLJ
 * @date 2022/7/21
 * @time 12:59
 * @Description 基于mqtt v5的自动配置 ...
 *
 *  mqtt v5:
 *      inbound:
 *          - Mqttv5PahoMessageHandler
 *      outbound:
 *          - Mqttv5PahoMessageDrivenChannelAdapter
 *
 *      error:
 *          	It is recommended to have the MqttConnectionOptions#setAutomaticReconnect(boolean) set to true to let an internal IMqttAsyncClient instance to handle reconnects.
 *          	Otherwise, only the manual restart of Mqttv5PahoMessageDrivenChannelAdapter can handle reconnects,
 *          	e.g. via MqttConnectionFailedEvent handling on disconnection.
 *
 *
 */
@Configuration
@AutoConfigureAfter(MqttV3AutoConfiguration.class)
@ConditionalOnClass(Mqttv5PahoMessageHandler.class)
public class MqttV5AutoConfiguration {
    /**
     * @param mqttProperties mqtt init properties ...
     * @return connection Options
     */
    @Bean
    @ConditionalOnMissingBean(name = "mqttConnectionOptions")
    @ConfigurationProperties(prefix = "lightning.mqtt.v5.connection-options")
    public MqttConnectionOptions mqttConnectionOptions(MqttProperties mqttProperties) {
        MqttConnectionOptions mqttConnectionOptions = new MqttConnectionOptions();
        MqttProperties.BaseConfigInfo baseInfo = mqttProperties.getBaseInfo();
        // 本来连接选项中都可以配置userName / password ...
        if (baseInfo != null) {
            if (StringUtils.isNotBlank(baseInfo.getUserName())) {
                mqttConnectionOptions.setUserName(baseInfo.getUserName());
            }
            if (StringUtils.isNotBlank(baseInfo.getPassword())) {
                mqttConnectionOptions.setPassword(baseInfo.getPassword().getBytes(StandardCharsets.UTF_8));
            }
        }
        Boolean automaticReconnect = mqttProperties.getBaseInfo().getV5().getAutomaticReconnect();

        // 自动重连 ...
        if(automaticReconnect != null) {
            mqttConnectionOptions.setAutomaticReconnect(automaticReconnect);
        }
        else {
            mqttConnectionOptions.setAutomaticReconnect(true);
        }
        return mqttConnectionOptions;
    }

    /**
     * If connection fails on start up or at runtime,
     * the Mqttv5PahoMessageHandler tries to reconnect on the next message produced to this handler ...
     * <p>
     * if possible , If this manual reconnection fails, the connection is exception is thrown back to the caller. ..
     * In this case the standard Spring Integration error handling procedure is applied, including request handler advices,
     * e.g. retry or circuit breaker.
     *
     * @param mqttConnectionOptions mqtt connection options ...
     * @param mqttProperties        mqtt properties ..
     * @return 集成流 ...
     */
    @Bean
    public IntegrationFlow mqttOutFlow(MqttConnectionOptions mqttConnectionOptions, MqttProperties mqttProperties) {
        Mqttv5PahoMessageHandler messageHandler = new Mqttv5PahoMessageHandler(mqttConnectionOptions, mqttProperties.getOutBound().getClientId());
        MqttHeaderMapper mqttHeaderMapper = new MqttHeaderMapper();

        List<String> outboundHeaderNames = mqttProperties.getOutBound().getV5().getHeaderNames();
        List<String> defaultOutBoundHeaderNames = Arrays.asList("contentType", "mqtt_messageExpiryInterval", "mqtt_responseTopic", "mqtt_correlationData");
        if(outboundHeaderNames != null && outboundHeaderNames.size() > 0) {
            for (String outboundHeaderName : outboundHeaderNames) {
                // 设置 headers .... 需要从MqttHeaders中查看那些可以映射 ...
                if(!defaultOutBoundHeaderNames.contains(outboundHeaderName.trim())) {
                    mqttHeaderMapper.setOutboundHeaderNames(outboundHeaderName);
                }
            }
            // 设置默认的
            for (String defaultOutBoundHeaderName : defaultOutBoundHeaderNames) {
                mqttHeaderMapper.setOutboundHeaderNames(defaultOutBoundHeaderName);
            }
        }

        messageHandler.setHeaderMapper(mqttHeaderMapper);
        Boolean async = mqttProperties.getOutBound().getV5().getAsync();
        if ( async != null && async ) {
            messageHandler.setAsync(true);
            Boolean asyncEvents = mqttProperties.getOutBound().getV5().getAsyncEvents();
            if (asyncEvents != null && asyncEvents) {
                messageHandler.setAsyncEvents(true);
            }
        }

        // 将其他类型转换为byte[] ....
        messageHandler.setConverter(new ByteArrayMessageConverter());
        return f -> f.handle(messageHandler);
    }

    /**
     * The inbound channel adapter for the MQTT v5 protocol is present as an Mqttv5PahoMessageDrivenChannelAdapter.
     * It requires a clientId and MQTT broker URL or MqttConnectionOptions reference, plus topics to which to subscribe and consume from.
     * It supports a MqttClientPersistence option, which is in-memory by default.
     * The expected payloadType (byte[] by default) can be configured and it is propagated to the provided SmartMessageConverter for conversion from byte[] of the received MqttMessage.
     * If the manualAck option is set, then an IntegrationMessageHeaderAccessor.ACKNOWLEDGMENT_CALLBACK header is added to the message to produce as an instance of SimpleAcknowledgment.
     * The HeaderMapper<MqttProperties> is used to map PUBLISH frame properties (including user properties) into the target message headers.
     * Standard MqttMessage properties, such as qos, id, dup, retained, plus received topic are always mapped to headers.
     * See MqttHeaders for more information.
     *
     * HeaderMapper 将映射PUBLISH 帧属性(包括用户属性) 到目标的消息header中 ... MqttHeaders 展示了一些常见的header(也就是能够映射的,如果你需要映射,请添加配置属性) ....
     * 例如这里是 inbound ....
     * 请查看
     * @see MqttProperties#getInBound() 了解详情 ...
     * 注意:
     *  The org.springframework.integration.mqtt.support.MqttMessageConverter cannot be used with the Mqttv5PahoMessageDrivenChannelAdapter
     *  since its contract is aimed only for the MQTT v3 protocol
     *
     * @param mqttProperties mqttProperties 参数 ...
     * @return 集成流 ...
     */
    @Bean
    public IntegrationFlow mqttInFlow(MqttConnectionOptions mqttConnectionOptions, MqttProperties mqttProperties) {
        Mqttv5PahoMessageDrivenChannelAdapter messageProducer =
                new Mqttv5PahoMessageDrivenChannelAdapter(mqttConnectionOptions,mqttProperties.getInBound().getClientId(),mqttProperties.getInBound().getTopics().toArray(new String[0]));

        List<String> headerNames = mqttProperties.getOutBound().getV5().getHeaderNames();

        if(headerNames != null && headerNames.size() > 0) {
            MqttHeaderMapper mqttHeaderMapper = new MqttHeaderMapper();
            // 这里不再填充默认headers ... 默认就是 * 全部 ...
            // 应该互斥 ...
            for (String headerName : headerNames) {
                mqttHeaderMapper.setInboundHeaderNames(headerName);
            }
        }

        /**
         * 负载类型 默认 byte[]
         * 如果配置了其他负载类型,需要提供SmartMessageConverter 进行负载转换 ...
         */
        MqttProperties.PayloadType payloadType = mqttProperties.getInBound().getV5().getPayloadType();
        if(payloadType != null) {
            messageProducer.setPayloadType(payloadType.getPayloadType());
            messageProducer.setMessageConverter(payloadType.getSmartMessageConverter());
        }
        Boolean manualAck = mqttProperties.getInBound().getV5().getManualAck();
        if(manualAck != null) {
            messageProducer.setManualAcks(manualAck);
        }
        return IntegrationFlows.from(messageProducer)
                // 设置输入管道 ...的名称 ..
                .channel(c -> c.queue("fromMqttChannel"))
                .get();
    }
}
