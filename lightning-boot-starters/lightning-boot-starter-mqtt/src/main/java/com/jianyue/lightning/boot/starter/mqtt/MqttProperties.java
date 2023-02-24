package com.jianyue.lightning.boot.starter.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;

import java.util.Collections;
import java.util.List;

/**
 * @author FLJ
 * @dateTime 2022/1/2 15:46
 * @description mqtt properties
 */
@ConfigurationProperties(prefix = "lightning.mqtt")
public class MqttProperties {


    private final InBoundProperties inBound;

    private final OutBoundProperties outBound;

    private final BaseConfigInfo baseInfo;

    /**
     * payload 是否作为bytes 呈现
     */
    private boolean payloadAsBytes;

    public MqttProperties() {
        this.inBound = new InBoundProperties();
        this.outBound = new OutBoundProperties();
        this.baseInfo = new BaseConfigInfo();
    }

    public InBoundProperties getInBound() {
        return inBound;
    }

    public OutBoundProperties getOutBound() {
        return outBound;
    }

    public BaseConfigInfo getBaseInfo() {
        return baseInfo;
    }

    public boolean isPayloadAsBytes() {
        return payloadAsBytes;
    }

    public void setPayloadAsBytes(boolean payloadAsBytes) {
        this.payloadAsBytes = payloadAsBytes;
    }


    @Data
    public static class InBoundProperties {

        /**
         * 客户端id
         */
        private String clientId;
        /**
         * 订阅的 topics
         */
        private List<String> topics = Collections.singletonList(OutBoundProperties.DEFAULT_TOPIC);
        /**
         * 完成时间超时,仅仅异步有效
         */
        private long completionTimeOut = 3000;

        /**
         * 异步
         */
        private boolean async  = false;
        /**
         * 服务质量 3种
         */
        private List<Long> qos = Collections.singletonList(1L);

        /**
         * v5 配置
         */
        private V5 v5 = new V5();

        @Data
        public static class V5 {
            /**
             * 负载类型,可不填,默认byte[]
             * 你想要接收的负载类型 ...
             */
            private PayloadType  payloadType;
            /**
             * 手动ack,默认false ...
             */
            private Boolean manualAck;

            /**
             * need map to inbound headers of target payload ...
             */
            private  List<String> headerNames;
        }


    }

    public static enum PayloadType {
        /**
         * 字符串
         */
        string {
            @Override
            public Class<?> getPayloadType() {
                return String.class;
            }

            @Override
            public SmartMessageConverter getSmartMessageConverter() {
                return new StringMessageConverter();
            }
        },
        /**
         * 字节数组
         */
        byteArray {
            @Override
            public Class<?> getPayloadType() {
                return byte[].class;
            }

            @Override
            public SmartMessageConverter getSmartMessageConverter() {
                return new ByteArrayMessageConverter();
            }
        };

        public abstract SmartMessageConverter getSmartMessageConverter();
        public abstract Class<?> getPayloadType();
    }

    @Data
    public static class OutBoundProperties {
        /**
         * 客户端id
         */
        private String clientId;

        /**
         * 异步
         */
        private boolean async  = false;

        /**
         * 默认topic
         */
        private String defaultTopic = DEFAULT_TOPIC;

        /**
         * 默认qos
         */
        private long defaultQos = 1;

        private final static  String DEFAULT_TOPIC = "default";

        private V5 v5 = new V5();

        @Data
        public static class V5 {
            /**
             * 手动ack,默认false ...
             */
            private Boolean manualAck;

            /**
             * 异步模式
             * 异步模式下才能使用异步事件 ...
             */
            private Boolean async;
            /**
             * 异步事件
             */
            private Boolean asyncEvents;

            /**
             * need map to out bound headers of target payload ...
             */
            private List<String> headerNames;
        }

    }

    @Data
    public static class BaseConfigInfo {

        private String userName;

        private String password;

        private V5 v5 = new V5();

        @Data
        public static class V5 {
            /**
             * 是否自动重连 ...
             */
            private Boolean automaticReconnect = true;
        }
    }
}


