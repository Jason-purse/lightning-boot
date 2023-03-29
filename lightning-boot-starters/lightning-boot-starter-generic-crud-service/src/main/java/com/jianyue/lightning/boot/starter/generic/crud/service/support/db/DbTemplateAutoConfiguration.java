package com.jianyue.lightning.boot.starter.generic.crud.service.support.db;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({JpaDbTemplate.class,MongoDbTemplate.class})
@Inherited
public @interface DbTemplateAutoConfiguration {
}
