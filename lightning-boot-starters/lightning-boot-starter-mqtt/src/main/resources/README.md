## mqtt
### v5支持
    org.eclipse.paho:org.eclipse.paho.mqttv5.client 作为一个Optional 依赖,如果目标项目需要,则可以加入,否则可以不加入 使用v3

    v3 引入 starter-mqtt 即可
    v5 额外引入
      ```text
      <dependency>
                  <groupId>org.springframework.integration</groupId>
                  <artifactId>spring-integration-jmx</artifactId>
              </dependency>
            <dependency>
                  <groupId>org.eclipse.paho</groupId>
                  <artifactId>org.eclipse.paho.mqttv5.client</artifactId>
                  <version>${mqtt.version}</version>
                  <scope>provided</scope>
                  <optional>true</optional>
              </dependency>
      ```
#### v5 mqtt message properties
- MqttHeaderMapper
  
    用来在发布或者接收动作上进行 to / from header映射  ...
  - 默认使用* 模式映射所有接收到的PUBLISH 帧属性(包括用户属性)
  - 在发布端映射这个帧属性的子集header ...(contentType,mqtt_messageExpiryInterval, mqtt_responseTopic, mqtt_correlationData)

- Mqttv5PahoMessageHandler
       
  输出管道的适配器的v5协议支持
  - 这个类需要 客户端id和MQTT broker URL 或者MqttConnectionOptions  引用对象...
  - 同样支持MqttClientPersistence 选项,进行数据持久化(在这种情况下可以进行async 且弹射MqttIntegrationEvent 事件对象 - asyncEvents),如果一个
    请求消息荷载是一个org.eclipse.paho.mqttv5.common.MqttMessage,它是通过内部的IMqttAsyncClient进行发送的,如果payload是一个byte[],表示作为需要发送的目标
    MqttMessage 的荷载 ... 如果是String,将转换为byte[] 进行发送...其他情况将代理到MessageConverter  ...
    它是一个名称为: IntegrationContextUtils.ARGUMENT_RESOLVER_MESSAGE_CONVERTER_BEAN_NAME  类型为ConfigurableCompositeMessageConverter Bean,可以从
    ioc 上下文中获取,注意的是当请求的消息荷载是一个MqttMessage,那么提供的HeaderMapper<MqttProperties> 将不会使用 ...