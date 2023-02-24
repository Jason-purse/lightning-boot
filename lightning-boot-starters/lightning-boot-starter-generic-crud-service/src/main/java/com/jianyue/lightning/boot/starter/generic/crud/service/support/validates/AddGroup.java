package com.jianyue.lightning.boot.starter.generic.crud.service.support.validates;

import org.springframework.validation.annotation.Validated;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Validated(ADD.class)
public @interface AddGroup {
}
