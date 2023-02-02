package com.jianyue.lightning.boot.starter.mqtt.v3;

import com.jianyue.lightning.boot.starter.mqtt.MqttAutoConfiguration;
import com.jianyue.lightning.boot.starter.mqtt.MqttProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * @author FLJ
 * @date 2022/7/21
 * @time 14:05
 * @Description mqtt v3 自动配置 ....
 */
@Configuration
@AutoConfigureAfter(MqttAutoConfiguration.class)
@ConditionalOnMissingClass("org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler")
public class MqttV3AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "mqttConnectOptions")
    @ConfigurationProperties(prefix = "lightning.mqtt.connection-options")
    @Primary
    public MqttConnectOptions mqttConnectOptions(MqttProperties mqttProperties) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        MqttProperties.BaseConfigInfo baseInfo = mqttProperties.getBaseInfo();
        // 本来连接选项中都可以配置userName / password ...
        if(baseInfo != null) {
            if(StringUtils.isNotBlank(baseInfo.getUserName())) {
                mqttConnectOptions.setUserName(baseInfo.getUserName());
            }
            if(StringUtils.isNotBlank(baseInfo.getPassword())) {
                mqttConnectOptions.setPassword(baseInfo.getPassword().toCharArray());
            }
        }
        return mqttConnectOptions;
    }

    /**
     * 创建MqttPahoClientFactory，设置MQTT Broker连接属性，如果使用SSL验证，也在这里设置。
     * @return factory
     */
    @Bean
    @ConditionalOnMissingBean(MqttPahoClientFactory.class)
    public MqttPahoClientFactory mqttClientFactory(MqttConnectOptions connectOptions) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        String[] serverURIs = connectOptions.getServerURIs();
        if(ArrayUtils.isEmpty(serverURIs)) {
            connectOptions.setServerURIs(new String[]{"tcp://127.0.0.1:1883"});
        }
        factory.setConnectionOptions(connectOptions);
        return factory;
    }



    @Bean
    @ConditionalOnMissingBean(name = "mqttInputChannel")
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnBean(name = "mqttInputChannel",value = MessageChannel.class)
    public MessageProducer inBound(MqttPahoClientFactory mqttClientFactory,MqttProperties mqttProperties) {
        MqttProperties.InBoundProperties inBound = mqttProperties.getInBound();
        String[] topics = inBound.getTopics().toArray(new String[0]);
        // Paho客户端消息驱动通道适配器，主要用来订阅主题
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                inBound.getClientId(),
                mqttClientFactory,
                topics);
        adapter.setCompletionTimeout(inBound.getCompletionTimeOut());

        adapter.setConverter(defaultPahoMessageConverter(mqttProperties));
        Long[] qosList = inBound.getQos().toArray(new Long[0]);
        int[] qosArray = new int[qosList.length];
        for (int i = 0; i < qosList.length; i++) {
            qosArray[i] = qosList[i].intValue();
        }
        adapter.setQos(qosArray); // 设置QoS
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ConditionalOnMissingBean(name = "mqttOutboundChannel")
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnBean(name = "mqttOutboundChannel",value = MessageChannel.class)
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler outbound(MqttPahoClientFactory mqttClientFactory,MqttProperties mqttProperties) {
        MqttProperties.OutBoundProperties outBound = mqttProperties.getOutBound();
        // 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                outBound.getClientId(),
                mqttClientFactory);
        messageHandler.setAsync(outBound.isAsync()); // 如果设置成true，即异步，发送消息时将不会阻塞。
        messageHandler.setDefaultTopic(outBound.getDefaultTopic());
        messageHandler.setDefaultQos((int)outBound.getDefaultQos()); // 设置默认QoS
        messageHandler.setConverter(defaultPahoMessageConverter(mqttProperties));
        return messageHandler;
    }

    /**
     * 这个bean 无法和Mqtt V5共同使用 ....
     * @param mqttProperties mqtt properties ...
     * @return message converter ...
     */
    @Bean
    @ConditionalOnMissingBean(DefaultPahoMessageConverter.class)
    public DefaultPahoMessageConverter defaultPahoMessageConverter(MqttProperties mqttProperties) {
        // Paho消息转换器
        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();
        defaultPahoMessageConverter.setPayloadAsBytes(mqttProperties.isPayloadAsBytes());
        return defaultPahoMessageConverter;
    }
}
