package com.jianyue.lightning.boot.starter.generic.crud.service.support.controller;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基于策略来开启验证分组的 aop 检查注解 !!!
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(ControllerStrategyWithValidationConfigImporter.class)
public @interface EnableStrategyWithValidationAnnotation {
}
