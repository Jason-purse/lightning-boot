package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy;

import org.springframework.validation.annotation.Validated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Validated(SELECT_BY_ID.class)
public @interface SelectByIdGroup {
}
