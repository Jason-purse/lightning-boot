package com.jianyue.lightning.boot.starter.generic.crud.service.support.validates;

import org.springframework.validation.annotation.Validated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Validated(SELECT_LIST.class)
public @interface SelectListGroup {
}
