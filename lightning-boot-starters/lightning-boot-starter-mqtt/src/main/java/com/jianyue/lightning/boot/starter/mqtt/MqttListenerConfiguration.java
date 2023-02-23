package com.jianyue.lightning.boot.starter.mqtt;

import com.jianyue.lightning.boot.starter.mqtt.annotations.MqttListener;
import com.jianyue.lightning.boot.starter.mqtt.annotations.MqttListeners;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;



/**
 * @author FLJ
 * @date 2022/8/10
 * @time 17:33
 * @Description Mqtt listener 配置
 * 将它放在最后,等待 对应的bean
 *
 * @see MqttListeners
 * @see MqttListener
 */
@Slf4j
@AutoConfigureOrder(Integer.MAX_VALUE)
public class MqttListenerConfiguration implements DisposableBean {

    private  static final Map<String,List<MethodWrapper>> methodListeners = new ConcurrentHashMap<>();

    @Bean
    public static InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor() {
        List<String> excludeLists = new LinkedList<>();
        excludeLists.add("equals");
        excludeLists.add("toString");
        excludeLists.add("hashCode");
        return new InstantiationAwareBeanPostProcessor() {
            // 初始化之后 进行类型上的方法解析
            @Override
            public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
                // 拿取原始的类 ..
                Class<?> userClass = ClassUtils.getUserClass(bean.getClass());
                if(userClass.isAnnotationPresent(MqttListeners.class)) {
                    Method[] declaredMethods = userClass.getDeclaredMethods();
                    for (Method declaredMethod : declaredMethods) {
                        boolean annotationPresent = declaredMethod.isAnnotationPresent(MqttListener.class);

                        if(!excludeLists.contains(declaredMethod.getName()) && annotationPresent) {
                            MqttListener declaredAnnotation = declaredMethod.getDeclaredAnnotation(MqttListener.class);
                            Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(declaredAnnotation);

                            // 判断参数是否应该要
                            int parameterCount = declaredMethod.getParameterCount();
                            String[] args = (String[]) annotationAttributes.get("args");

                            if(parameterCount <= 0) {
                                throw new IllegalArgumentException("at least exists one parameter !!");
                            }
                            // 表示应该要参数
                            if (parameterCount != 1 + args.length) {
                                args = null;
                            }
                            // 可以收集起来,然后进行处理
                            methodListeners.computeIfAbsent(annotationAttributes.get("value").toString(),(key) -> new LinkedList<>())
                                    .add(new MethodWrapper(Modifier.isStatic(declaredMethod.getModifiers()) ? null : bean,declaredMethod, args));
                        }
                    }
                }
                return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean,beanName);
            }

            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                // 拿取原始的类 ..
                Class<?> userClass = ClassUtils.getUserClass(bean.getClass());
                // 解析 MessageHandler(当前配置创建的,没必要代理) ...
                // 只处理handler
                if((beanName.equals("handler")) && MessageHandler.class.isAssignableFrom(userClass)) {
                    // 对于这个类,我们仅仅做出代理

                    InvocationHandler invocationHandler = (proxy, method, args) -> {
                        // 只有它先拦截处理 ...
                        if(method.getName().startsWith("handleMessage")) {
                            // 先执行
                            Message<?> arg = (Message<?>) args[0];
                            Object topic = arg.getHeaders().get("mqtt_receivedTopic");
                            assert  topic != null;
                            MqttListenerConfiguration.handleMqttMessage(topic.toString(), arg.getPayload().toString());
                        }
                        // 在执行之前,判断
                        return method.invoke(bean,args);
                    };

                    return  Proxy.newProxyInstance(bean.getClass().getClassLoader(), new Class[]{MessageHandler.class},invocationHandler);
                }
                return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean,beanName);
            }
        };


    }

    /**
     * 如果没有这个,就直接创建一个 ...
     * 内部名称为 internalMessageHandler
     * @return
     */
    @Bean(name = "$$$$$$$$internalMessageHandler$$$$$$$$")
    // ServiceActivator注解表明：当前方法用于处理MQTT消息，inputChannel参数指定了用于消费消息的channel。
    @ConditionalOnBean(name = "mqttInputChannel", value = MessageChannel.class) // 保证在任何用户的
    @ServiceActivator(inputChannel = "mqttInputChannel")
    @Primary
    public MessageHandler handler() {
        return message -> {
            String payload = message.getPayload().toString();
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
            log.info("payload: {},topic: {}", payload, topic);
            try {
                handleMqttMessage(topic, payload);
            } catch (Exception e) { // 报错之后,整个客户端都会挂掉,进行日志处理..
                // pass
                e.printStackTrace();
                log.info("mqtt handle message occur error: {}", e.getMessage());
            }
        };
    }

    static void handleMqttMessage(String topic,String payload) {
        List<MethodWrapper> orDefault = methodListeners.getOrDefault(topic.trim(), Collections.emptyList());
        if(!orDefault.isEmpty()) {
            for (MethodWrapper methodWrapper : orDefault) {
                methodWrapper.invoke(topic,payload);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        methodListeners.clear();
    }

    /**
     * 方法包装器
     */
    static class MethodWrapper {

        private Object targetObject;

        private Method method;

        private List<Object> args;

        public MethodWrapper(Object targetObject,Method method,Object[] args) {
            this.targetObject = targetObject;
            this.method = method;
            this.args = args == null ? Collections.emptyList() : Arrays.asList(args);
        }

        public Object getTargetObject() {
            return targetObject;
        }

        public void setTargetObject(Object targetObject) {
            this.targetObject = targetObject;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Object[] getArgs() {
            return args.toArray();
        }

        public void setArgs(Object[] args) {
            this.args = Arrays.asList(args);
        }

        public void invoke(String topic,String payload) {
           try {
               if(!args.isEmpty()) {
                   LinkedList<Object> objects = new LinkedList<>(args);
                   // 放在第一位
                   objects.add(0,payload);
                   method.invoke(targetObject,objects.toArray());
               }
               else  {
                   method.invoke(targetObject,payload);
               }
           }catch (Exception e) {
               // pass
               e.printStackTrace();
           }
        }
    }

}
