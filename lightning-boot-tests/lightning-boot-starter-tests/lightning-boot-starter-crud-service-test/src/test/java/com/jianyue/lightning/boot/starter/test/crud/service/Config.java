package com.jianyue.lightning.boot.starter.test.crud.service;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.MongoDbTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class Config {

    @Bean
    public MongoDbTemplate mongoDbTemplate(MongoTemplate mongoTemplate) {
        return new MongoDbTemplate(mongoTemplate);
    }

    @Bean
    public DefaultObjectService defaultObjectService() {
        return new DefaultObjectService();
    }

}