package com.jianyue.lightning.boot.starter.generic.crud.service;

import com.jianyue.lightning.boot.autoconfigure.web.WebConfigAutoConfiguration;
import com.jianyue.lightning.boot.starter.generic.crud.service.model.params.OrganizationParam;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.DBTemplate;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.ParamHandlerMethodArgumentResolver;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.AbstractCrudService;
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringJUnitWebConfig
public class ParamHandlerMethodArgumentResolverTests {

    @Configuration
    @Import({ WebConfigAutoConfiguration.class,WebMvcAutoConfiguration.class})
    public static class MyParamHandlerConfig {

        @Bean
        public MyParamService myParamService() {
            return new MyParamService(dbTemplate());
        }
        @Bean
        public DBTemplate dbTemplate() {
            return Mockito.mock(DBTemplate.class);
        }

        @Bean
        public MyParamController myParamController() {
            return new MyParamController(myParamService());
        }

        @Bean
        public ParamHandlerMethodArgumentResolver paramHandlerMethodArgumentResolver() {
            return new ParamHandlerMethodArgumentResolver(Set.of("com.jianyue.lightning.boot.starter.generic.crud.service"));
        }

        @Bean
        public OrganizationController organizationController() {
            return new OrganizationController(organizationService());
        }

        @Bean
        public OrganizationService organizationService() {
            return Mockito.mock(OrganizationService.class);
        }
    }

    public static class MyParamService extends AbstractCrudService<Param, Entity<String>> {
        public MyParamService(DBTemplate dbTemplate) {
            super(dbTemplate);
        }

    }
    public static class OrganizationService extends AbstractCrudService<OrganizationParam,Entity<String>> {

    }

    @RestController
    @RequestMapping("/api")
    public static class MyParamController extends AbstractGenericController<Param,MyParamService> {

        public MyParamController(@NotNull MyParamService service) {
            super(service);
        }
    }

    @RestController
    @RequestMapping("/api/organization")
    public static class OrganizationController extends AbstractGenericController<OrganizationParam,OrganizationService> {

        public OrganizationController(@NotNull OrganizationService service) {
            super(service);
        }
    }

    protected static MockMvc mockMvc;
    @BeforeEach
    public void config(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    @Test
    public void test() throws Exception {

        MvcResult username = mockMvc.perform(MockMvcRequestBuilders.get("/api").param("username", "1231"))
                .andReturn();

        Exception resolvedException = username.getResolvedException();
        if(resolvedException != null) {
            resolvedException.printStackTrace();
        }
    }

    @Test
    public void organizationParamTest() throws Exception {
        MvcResult username = mockMvc.perform(MockMvcRequestBuilders.get("/api/organization").param("username", "1231"))
                .andReturn();

        Exception resolvedException = username.getResolvedException();
        if(resolvedException != null) {
            resolvedException.printStackTrace();
        }

        MvcResult complex = mockMvc.perform(MockMvcRequestBuilders.get("/api/organization")
                        .param("username", "1231")
                        .param("password","12345")
                        .param("strategyKey","complex"))
                .andReturn();

        resolvedException = complex.getResolvedException();
        if(resolvedException != null) {
            resolvedException.printStackTrace();
        }
    }
}
