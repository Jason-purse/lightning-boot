package com.jianyue.lightning.boot.starter.mqtt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author FLJ
 * @date 2022/8/10
 * @time 17:39
 * @Description 标记注解 ...
 *
 * 只需要标记它,进行这个bean的 方法扫描,将@MqttListener 的注解的方法作为 监听器方法 ...
 *
 * for example:
 * @Configuration
 * @MqttListeners
 * public class TestConfiguration {
 *
 *   // mqtt listeners ..
 *
 *   // the  args attribute of annotation to aim method parameter args of under method;
 *   @MqttListener(value = "Camera",[args = ....])
 *   public static void handleMessageForCameraTopic(String payload,[... args]) {
 *      ...
 *   }
 *
 *   or
 *
 *   @MqttListener(value = "Camera",[args = ...])
 *   public void handleMessageForCameraTopic1(String payload,[...args]) {
 *      ....
 *   }
 * }
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqttListeners {

}
