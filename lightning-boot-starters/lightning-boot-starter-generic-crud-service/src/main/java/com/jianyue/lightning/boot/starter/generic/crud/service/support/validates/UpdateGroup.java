package com.jianyue.lightning.boot.starter.generic.crud.service.support.validates;

import org.springframework.validation.annotation.Validated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Validated(UPDATE.class)
public @interface UpdateGroup {
}
