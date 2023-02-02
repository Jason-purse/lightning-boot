package com.jianyue.lightning.boot.starter.mqtt.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author FLJ
 * @date 2022/8/10
 * @time 17:31
 * @Description 基于注解形式的Mqtt Listener
 *
 * 注解的方法形式为
 * [modifier] * methodName(payload,[...args])
 *
 * @see MqttListeners
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqttListener {
    /**
     * 等同于 topic
     */
    @AliasFor("topic")
    String value() default "";

    /**
     * topic
     */
    @AliasFor("value")
    String topic() default "";


    /**
     * 一些标记型参数(常量参数)
     */
    String[] args() default {};
}
