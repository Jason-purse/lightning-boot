package com.jianyue.lightning.boot.starter.test.crud.service;

import com.jianyue.lightning.boot.starter.generic.crud.service.entity.StringBasedMongoEntity;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.DefaultMongoQuery;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.MongoQueryInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {MongoAutoConfiguration.class,MongoDataAutoConfiguration.class, Config.class})
@TestPropertySource(properties = {
        "spring.data.mongodb.uri:mongodb://192.168.0.235:37017,192.168.0.235:37018,192.168.0.235:37019/safedb?replicaSet=sfrs0",
        "spring.data.mongodb.username:root",
        "spring.data.mongodb.password:6tfc^YHN"
})
public class ServiceTests {

    @Autowired
    DefaultObjectService defaultObjectService;

    @Document("j_question")
    public static class MyTestClass extends StringBasedMongoEntity {

    }

    @Test
    public void test() {
        Assertions.assertNotNull(defaultObjectService);

        final int size = defaultObjectService.getDbTemplate()
                .selectByComplex(
                        new DefaultMongoQuery(
                                new MongoQueryInfo(
                                        new Query()
                                )
                        ),
                        MyTestClass.class
                )
                .size();

        System.out.println("data size " + size);
        Assertions.assertTrue(size > 0);
    }

}
