package com.jianyue.lightning.framework.design.pattern.strategy;

import org.springframework.context.annotation.Bean;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 14:42
 * @Description 设计模式配置类
 */
public class DesignPatternConfiguration {

    /**
     * 策略模式 .. 处理 / 后置处理器
     */
    @Bean
    public static StrategyServiceBeanPostProcessor beanPostProcessor() {
        return new StrategyServiceBeanPostProcessor();
    }
}
