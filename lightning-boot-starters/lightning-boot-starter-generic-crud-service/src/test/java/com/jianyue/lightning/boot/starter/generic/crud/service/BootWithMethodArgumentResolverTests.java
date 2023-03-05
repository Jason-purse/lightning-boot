//package com.jianyue.lightning.boot.starter.generic.crud.service;
//
//import com.jianyue.lightning.boot.autoconfigure.web.WebConfigAutoConfiguration;
//import com.jianyue.lightning.boot.starter.generic.crud.service.config.CrudServiceAutoConfiguration;
//import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController;
//import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.DBTemplate;
//import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
//import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.ParamHandlerMethodArgumentResolver;
////import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.SimpleForGenericCrudHandlerMethodArgumentResolverHandler;
//import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.AbstractCrudService;
//import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
//import com.jianyue.lightning.framework.web.config.ProfilerRequestMappingHandlerAdapter;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
//import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.event.annotation.BeforeTestMethod;
//import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MockMvcBuilder;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
//import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
//
//import java.net.URI;
//import java.util.Optional;
//
///**
// * @author FLJ
// * @date 2023/2/23
// * @time 14:10
// * @Description boot 与 method arguments resolver  intergration tests
// */
//@SpringBootTest
//public class BootWithMethodArgumentResolverTests {
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Builder
//    static class UserParam implements Param {
//
//        private String username;
//
//        private String password;
//    }
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Builder
//    static class UserEntity implements Entity {
//        private String username;
//
//        private String password;
//
//        @Override
//        public void saveFill() {
//
//        }
//
//        @Override
//        public void updateFill() {
//
//        }
//    }
//
//    static class MyUserParamService extends AbstractCrudService<UserParam,UserEntity> {
//        public MyUserParamService(DBTemplate dbTemplate) {
//            super(dbTemplate);
//        }
//    }
//
////    @Import(CrudServiceAutoConfiguration.class)
//    @org.springframework.context.annotation.Configuration
//    @TestPropertySource("classpath:boot-with-method-argument-resolver.properties")
//    @SpringJUnitWebConfig
//    public static class MethodArgumentResolverConfiguration {
//        @org.springframework.context.annotation.Configuration
//        @Import({WebConfigAutoConfiguration.class, WebMvcAutoConfiguration.class})
//        public static class Config {
//
//
//            @Bean
//            public ParamHandlerMethodArgumentResolver paramHandlerMethodArgumentResolver(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
//                Optional<HandlerMethodArgumentResolver> first = requestMappingHandlerAdapter.getArgumentResolvers().stream().filter(ele -> {
//                            return ele instanceof RequestResponseBodyMethodProcessor;
//                        })
//                        .findFirst();
//                assert first.isPresent();
//                return new ParamHandlerMethodArgumentResolver(((RequestResponseBodyMethodProcessor) first.get()));
//            }
//
//
//            @Bean
//            public DBTemplate dbTemplate() {
//                return Mockito.mock(DBTemplate.class);
//            }
//
//            @Bean
//            public Configuration configuration() {
//                return new Configuration(myUserParamService());
//            }
//
//            @Bean
//            public MyUserParamService myUserParamService() {
//                return new MyUserParamService(dbTemplate());
//            }
//
//            @Bean
//            public InterfaceConfiguration interfaceConfiguration() {
//                return new InterfaceConfiguration(organizationService());
//            }
//
//            @Bean
//            public OrganizationService organizationService() {
//                return new OrganizationService(dbTemplate());
//            }
//
//    }
//
//
//    @Autowired
//    private WebApplicationContext applicationContext;
//
//    private  MockMvc mockMvc;
//    @BeforeEach
//    public void before() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(
//                applicationContext).build();
//    }
//
//
//    @Test
//    public void test() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/").param("username","12345"))
//                .andReturn();
//    }
//
//    @Test
//    public void interfaceTest() throws Exception {
//        MvcResult username = mockMvc.perform(MockMvcRequestBuilders.get("/api/v2").param("username", "12345"))
//                .andReturn();
//
//        Exception resolvedException = username.getResolvedException();
//        if(resolvedException != null) {
//            resolvedException.printStackTrace();
//        }
//    }
//
//}
//    @RequestMapping("/api")
//    @RestController
//    public static class Configuration extends AbstractGenericController<UserParam,MyUserParamService> {
//
//
//
//        public Configuration(MyUserParamService myUserParamService) {
//            super(myUserParamService);
//        }
//
//    }
//
//    interface OrganizationParam extends Param {
//
//    }
//    static class OrganizationService extends AbstractCrudService<Param,Entity> {
//
//        public OrganizationService(DBTemplate dbTemplate) {
//            super(dbTemplate);
//        }
//    }
//
//    @RequestMapping("/api/v2")
//    @RestController
//    public static class InterfaceConfiguration extends AbstractGenericController<Param,OrganizationService> {
//
//        public InterfaceConfiguration(@NotNull OrganizationService service) {
//            super(service);
//        }
//    }
//
//
//
//}
