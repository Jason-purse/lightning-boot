package com.jianyue.lightning.boot.starter.generic.crud.service.config

import com.jianyue.lightning.boot.autoconfigure.web.EnableBaseFactoryHandlerMethodArgumentResolver
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.EnableControllerValidationStrategy
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.JpaDbTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.MongoDbTemplate
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerFactoryConfigurer
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerMethodArgumentResolver
import com.jianyue.lightning.framework.web.method.argument.resolver.HandlerMethodArgumentResolverHandlerProvider
import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.SimpleForGenericCrudHandlerMethodArgumentResolverHandler
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import com.jianyue.lightning.framework.web.method.argument.resolver.DefaultHandlerMethodArgumentResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.core.MethodParameter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 主要负责通用crud 的{@link Param}的实例化方式 ...
 *
 * 能基于 FactoryBasedHandlerMethodArgumentResolver 实现 策略工厂实现 参数解析 !!!
 * @see FactoryBasedHandlerMethodArgumentResolver
 */
@ConditionalOnClass(WebMvcConfigurer::class)
@EnableControllerValidationStrategy
@EnableBaseFactoryHandlerMethodArgumentResolver
@Import(MongoDbTemplate::class, JpaDbTemplate::class)
@AutoConfigureAfter(value = [MongoDataAutoConfiguration::class, JpaRepositoriesAutoConfiguration::class])
class CrudServiceAutoConfiguration : WebMvcConfigurer {
    private val simpleHandlerMethodArgumentResolverHandler =
        SimpleForGenericCrudHandlerMethodArgumentResolverHandler()


    /**
     * 自动配置 !!!
     */
    @Autowired(required = false)
    fun configureGenericCrudHandlerMethodArgumentResolver(vararg configurers: GenericCrudHandlerMethodArgumentResolverConfigurer) {
        for (configurer in configurers) {
            configurer.configure(simpleHandlerMethodArgumentResolverHandler);
        }
    }


    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        // 直接将 参数解析器 处理器 放入工厂中 !!!!
        // 追加到工厂中 !!!
        resolvers.filterIsInstance<FactoryBasedHandlerMethodArgumentResolver>().let {
            if (it.isNotEmpty()) {
                it.forEach {
                    // 针对 Param java类的处理 ...
                    it.addArgumentResolverHandlers(
                        HandlerMethodArgumentResolverHandlerProvider(
                            Param::class.java,
                            simpleHandlerMethodArgumentResolverHandler,
                            simpleHandlerMethodArgumentResolverHandler.predicate
                        )
                    )
                }

            } else {
                // 也就是直接追加 !!
                resolvers.add(
                    DefaultHandlerMethodArgumentResolver(
                        simpleHandlerMethodArgumentResolverHandler.predicate,
                        simpleHandlerMethodArgumentResolverHandler
                    )
                )
            }
        }

    }

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // 继承 mvc jackson 的配置选项
        // JacksonHttpMessageConvertersConfiguration
        converters.add(1, simpleHandlerMethodArgumentResolverHandler.messageConverter)
    }
}