package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.ValidationAnnotation;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Validated(ADD.class)
@ValidationAnnotation
public @interface AddGroup {
}
