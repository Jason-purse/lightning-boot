package com.jianyue.lightning.boot.starter.generic.crud.service.support.validates;

import java.lang.annotation.*;

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 16:16
 * @Description 是否启用通用CRUD的 aop 代理(实现ValidationSupport)
 * @see com.jianyue.lightning.boot.starter.generic.crud.service.config.AopConfig
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ValidationAnnotation {

}
