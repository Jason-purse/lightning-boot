package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author FLJ
 * @date 2023/3/2
 * @time 10:58
 * @Description 开启去启动 {@link com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController}
 * 对{@link DefaultStrategySupportAdapter}的支持 !!!
 * <p>
 * 但是{@link com.jianyue.lightning.boot.starter.generic.crud.service.support.service.CrudService} 可以不支持这个特性 !!!
 * <p>
 * 使用它之后,控制器的对应方法将会设置{@link StrategyGroupSupport.Companion#getValidationGroup()} 的线程变量 !!!
 * <p>
 * 查看{@link StrategyGroup} 了解所有内置的策略分组,这些策略分组将会在控制器方法参数构造时自动执行属性校验 !!!
 *
 * 如果不想要基于策略分组校验!! 可以不使用 {@link com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableControllerValidationStrategy {

}
