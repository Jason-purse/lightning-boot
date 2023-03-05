package com.jianyue.lightning.boot.starter.generic.crud.service;

import com.jianyue.lightning.boot.starter.generic.crud.service.config.ControllerValidationAopAspectConfiguration;
import com.jianyue.lightning.boot.starter.generic.crud.service.config.CrudServiceAutoConfiguration;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.ADD;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.SELECT_LIST;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.UPDATE;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.DBTemplate;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.AbstractCrudService;
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
import com.jianyue.lightning.framework.web.method.argument.context.MethodArgumentContext;
import com.jianyue.lightning.framework.web.method.argument.resolver.*;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Predicate;

public class CrudControllerBootTests {
    @Data
    public static class MyParam implements Param {
        @NotNull(groups = {SELECT_LIST.class, UPDATE.class, ADD.class})
        private String username;

        private String password;

        private String origin;
    }


    @Data
    public static class ProvinceParam implements Param {
        @NotNull(groups = {SELECT_LIST.class, UPDATE.class, ADD.class})
        private String username;

        private String password;

        private List<String> userIds;
    }

    @Data
    public static class CityParam implements Param {
        private String password;
        private List<String> userIds;
        private String userId;
    }

    public static class MyService extends AbstractCrudService<MyParam, Entity> {

    }

    @RestController
    @RequestMapping("/api")
    public static class MyGenericController extends AbstractGenericController<MyParam,MyService> {

        public MyGenericController(@NotNull MyService service) {
            super(service);
        }
    }
    @SpringJUnitWebConfig
    public static class CrudControllersConfigTests {

        @Configuration
        @TestPropertySource({"classpath:boot-with-crud-controller.properties","classpath:application.properties"})
        @EnableWebMvc
        @Import({DbTemplateMock.class,CrudServiceAutoConfiguration.class})
        public static class Config {

            //@Bean
            //public JpaDbTemplate jpaDbTemplate(JpaContext jpaContext) {
            //    return new JpaDbTemplate(jpaContext);
            //}

            @Bean
            public MyGenericController myGenericController(MyService myService) {
                return new MyGenericController(myService);
            }

            @Bean
            public MyService mockMyService() {
                return Mockito.mock(MyService.class);
            }

//            @Bean
//            public FactoryBasedHandlerMethodArgumentResolverConfigurer configurer() {
//                return new FactoryBasedHandlerMethodArgumentResolverConfigurer() {
//                    @Override
//                    public void configMethodArgumentResolver(FactoryBasedHandlerMethodArgumentResolver methodArgumentResolver) {
//                        methodArgumentResolver.addArgumentResolverHandlers(new DefaultFactoryBasedHMArgumentResolverHandlerProvider<>(
//                                Param.class,
//                                new FactoryBasedHMArgumentResolverHandler() {
//                                    @Override
//                                    public Object get(MethodArgumentContext value) throws Exception {
//                                        String type = value.getRequest().getParameter("type");
//                                        MyParam myParam = new MyParam();
//                                        WebDataBinder binder = value.getBinderFactory().createBinder(value.getRequest(), myParam, "");
//                                        binder.bind(new MutablePropertyValues(value.getRequest().getParameterMap()));
//
//                                        if(type.equals("province")) {
//                                            return new MyParam() {{
//                                                myParam.setOrigin("app");
//                                            }};
//
//
//                                        }
//                                        else {
//                                            return new MyParam() {{
//                                              myParam.setOrigin("invalid-origin");
//                                            }};
//                                        }
//                                    }
//                                },
//                                new Predicate<MethodParameter>() {
//                                    @Override
//                                    public boolean test(MethodParameter methodParameter) {
//                                        return true;
//                                    }
//                                }
//                        ));
//                    }
//                };
//            }
        }

        @Configuration
        public static class DbTemplateMock {
            @Bean
            public DBTemplate dbTemplate() {
                return Mockito.mock(DBTemplate.class);
            }
        }

        MockMvc mockMvc;

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        private ControllerValidationAopAspectConfiguration configuration;

        @BeforeEach
        void setup() {
            /**
             * 单机启动 !!
             */
            this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @Test
        public void test() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/list").param("username","1231412")
                            .param("password","13456")
                            .param("type","1234"))
                    .andReturn();
            //dispatcherServlet.service(new MockHttpServletRequest("GET","/api/list"),new MockHttpServletResponse());



        }
    }

}
