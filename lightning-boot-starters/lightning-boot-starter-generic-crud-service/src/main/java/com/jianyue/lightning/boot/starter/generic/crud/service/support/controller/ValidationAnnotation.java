package com.jianyue.lightning.boot.starter.generic.crud.service.support.controller;

import java.lang.annotation.*;

/**
 * @author FLJ
 * @date 2023/3/2
 * @time 13:48
 * @Description  联合 策略转换器 实现 分组校验 !!!
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ValidationAnnotation {
}
