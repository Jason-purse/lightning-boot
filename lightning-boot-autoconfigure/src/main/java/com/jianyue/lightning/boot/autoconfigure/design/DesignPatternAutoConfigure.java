package com.jianyue.lightning.boot.autoconfigure.design;

import com.jianyue.lightning.boot.starter.design.patterns.strategy.DesignPatternConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 14:41
 * @Description 设计模式自动配置
 */
@ConditionalOnClass(DesignPatternConfiguration.class)
@Import(DesignPatternConfiguration.class)
public class DesignPatternAutoConfigure {

}
