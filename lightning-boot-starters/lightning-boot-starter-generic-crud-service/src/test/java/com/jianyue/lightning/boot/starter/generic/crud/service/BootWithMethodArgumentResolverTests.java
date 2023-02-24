package com.jianyue.lightning.boot.starter.generic.crud.service;

import com.jianyue.lightning.boot.starter.generic.crud.service.config.CrudServiceAutoConfiguration;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.AbstractCrudService;
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

/**
 * @author FLJ
 * @date 2023/2/23
 * @time 14:10
 * @Description boot 与 method arguments resolver  intergration tests
 */
@SpringBootTest
public class BootWithMethodArgumentResolverTests {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class UserParam implements Param {

        private String username;

        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class UserEntity implements Entity {
        private String username;

        private String password;

        @Override
        public void saveFill() {

        }

        @Override
        public void updateFill() {

        }
    }

    static class MyUserParamService extends AbstractCrudService<UserParam,UserEntity> {

    }

    @Import(CrudServiceAutoConfiguration.class)
    @org.springframework.context.annotation.Configuration
    @TestPropertySource("classpath:boot-with-method-argument-resolver.properties")
    public static class Configuration extends AbstractGenericController<UserParam,MyUserParamService> {

        public Configuration() {
            super(new MyUserParamService());
        }
    }



    @Autowired
    private Configuration configuration;


    @Test
    public void test() {

    }
}
