package com.jianyue.lightning.boot.starter.generic.crud.service.config;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.JpaDbTemplate;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.MongoDbTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author FLJ
 * @date 2022/12/28
 * @time 11:26
 * @Description crud service 自动配置
 */
@Import({MongoDbTemplate.class, JpaDbTemplate.class,AopConfig.class})
@AutoConfigureAfter(value = {MongoDataAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class CrudServiceAutoConfiguration {

}
