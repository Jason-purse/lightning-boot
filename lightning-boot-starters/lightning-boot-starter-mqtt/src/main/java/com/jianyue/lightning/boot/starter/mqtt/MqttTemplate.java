package com.jianyue.lightning.boot.starter.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * @author FLJ
 * @dateTime 2022/1/2 17:00
 * @description 默认输出管道的Template...
 */
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttTemplate {
    // 定义重载方法，用于消息发送
    void sendToMqtt(String payload);
    // 指定topic进行消息发送
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, byte[] payload);
}