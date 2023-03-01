package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy;

import org.springframework.validation.annotation.Validated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author FLJ
 * @date 2023/3/1
 * @time 16:10
 * @Description 选择其中一个 !!!
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Validated(SELECT_ONE.class)
public @interface SelectOneGroup {
}
