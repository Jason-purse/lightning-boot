package com.jianyue.lightning.boot.starter.generic.crud.service;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.ControllerSupport;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.DBTemplate;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.SimpleForGenericCrudHandlerMethodArgumentResolverHandler;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.result.CrudResult;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.CrudService;
import com.jianyue.lightning.boot.starter.util.dataflow.impl.InputContext;
import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple;
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerMethodArgumentResolver;
import com.jianyue.lightning.framework.web.method.argument.resolver.HandlerMethodArgumentResolverHandlerProvider;
import com.jianyue.lightning.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * @author FLJ
 * @date 2023/2/23
 * @time 13:00
 * @Description 使用这个处理器的前提是,需要它具有实现类,且
 */
@SpringJUnitWebConfig
public class MethodArgumentResolvertests extends AbstractGenericController<MethodArgumentResolvertests.User, MethodArgumentResolvertests.MyService> {

    public MethodArgumentResolvertests() {
        super(new MyService() {
            @NotNull
            @Override
            public CrudResult addOperation(@NotNull InputContext<User> context) {
                return null;
            }

            @NotNull
            @Override
            public CrudResult addOperations(@NotNull InputContext<List<User>> context) {
                return null;
            }

            @NotNull
            @Override
            public CrudResult saveOperation(@NotNull InputContext<User> context) {
                return null;
            }

            @NotNull
            @Override
            public CrudResult selectOperation(@NotNull InputContext<User> context) {
                return null;
            }

            @NotNull
            @Override
            public CrudResult deleteOperation(@NotNull InputContext<User> context) {
                return null;
            }

            @NotNull
            @Override
            public CrudResult selectOperationById(@NotNull InputContext<User> context) {
                return null;
            }

            @NotNull
            @Override
            public CrudResult deleteOperationById(@NotNull InputContext<User> context) {
                return null;
            }

            @NotNull
            public DBTemplate getDbTemplate() {
                return null;
            }

            @NotNull
            @Override
            public Class<? extends Entity> getEntityClass() {
                return null;
            }

            @NotNull
            @Override
            public Class<? extends Param> getParamClass() {
                return null;
            }
        });
    }

    interface MyService extends CrudService<User> {

    }
    @Configuration
    public static class Config {

        @Bean
        public ConversionService conversionService() {
            return new DefaultConversionService();
        }
    }

    public void getAll(Param param) {

    }

    public void getForBody(@RequestBody Param param) {

    }


    @Autowired
    private ConversionService conversionService;

    private final FactoryBasedHandlerMethodArgumentResolver factoryBasedHandlerMethodArgumentResolver = new FactoryBasedHandlerMethodArgumentResolver().addArgumentResolverHandlers(
            new HandlerMethodArgumentResolverHandlerProvider<>(
                    Param.class,
                    new SimpleForGenericCrudHandlerMethodArgumentResolverHandler(),
                    methodParameter -> ControllerSupport.Companion.getParamClassState().get() != null && Param.class.isAssignableFrom(methodParameter.getParameterType())
            )
    );
    @Test
    public void test() throws Exception {

        MethodParameter parameter = MethodParameter.forExecutable(
                MethodArgumentResolvertests.class.getMethod("getAll", Param.class), 0
        );
        fromMethodParam(parameter);
    }

    private void fromMethodParam(MethodParameter parameter) throws Exception {
        MockHttpServletRequest post = new MockHttpServletRequest("post", "/api/admin/test");
        post.setParameters(new LinkedHashMap<>() {{
            put("username","1231");
            put("password","123423");
        }});

        // 开启具体实现类
        ControllerSupport.Companion.getParamClassState().set(new Tuple<>(
                User.class,null
        ));

        ConfigurableWebBindingInitializer configurableWebBindingInitializer = new ConfigurableWebBindingInitializer();
        configurableWebBindingInitializer.setConversionService(conversionService);

        if (factoryBasedHandlerMethodArgumentResolver.supportsParameter(parameter)) {
            Object argument = factoryBasedHandlerMethodArgumentResolver
                    .resolveArgument(
                            parameter
                            , new ModelAndViewContainer(),
                            new ServletWebRequest(post),
                            new ServletRequestDataBinderFactory(null,configurableWebBindingInitializer)
                    );
            Assert.notNull(argument,"must not be null !!!!");

            System.out.println(argument);
        }else {
            System.out.println("can't support !!!!");
        }
    }


    @Test
    public void forBodyTest() throws NoSuchMethodException, IOException {

        MethodParameter parameter = MethodParameter.forExecutable(
                MethodArgumentResolvertests.class.getMethod("getForBody", Param.class),
                0
        );

        ControllerSupport.Companion.getParamClassState().set(new Tuple<>(
                User.class,null
        ));

        // 使用消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new SimpleForGenericCrudHandlerMethodArgumentResolverHandler().getMessageConverter();
        if (messageConverter.canRead(parameter.getParameterType(), MediaType.APPLICATION_JSON)) {
            MockHttpServletRequest request = new MockHttpServletRequest("post", "/get/forbody");
            request.setContent(
                    JsonUtil.getDefaultJsonUtil().asJSON(new User(
                            "1231",
                            "password"
                    )).getBytes()
            );
            Object read = messageConverter.read(parameter.getParameterType(),
                    new ServletServerHttpRequest(request));
            Assert.notNull(read,"must not be null !!!!");

            System.out.println(read);
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User implements Param {
        private String username;

        private String password;
    }

}
