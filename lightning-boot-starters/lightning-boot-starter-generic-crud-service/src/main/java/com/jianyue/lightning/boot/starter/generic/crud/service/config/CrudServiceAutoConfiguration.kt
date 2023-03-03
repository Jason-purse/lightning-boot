package com.jianyue.lightning.boot.starter.generic.crud.service.config

import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.EnableControllerValidationStrategy
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.JpaDbTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.MongoDbTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.SimpleForGenericCrudHandlerMethodArgumentResolverHandler
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import com.jianyue.lightning.framework.web.method.argument.resolver.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.MethodParameter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 主要负责通用crud 的{@link Param}的实例化方式 ...
 *
 * 能基于 FactoryBasedHandlerMethodArgumentResolver 实现 策略工厂实现 参数解析 !!!
 * @see FactoryBasedHandlerMethodArgumentResolver
 */
@ConditionalOnClass(WebMvcConfigurer::class)
@EnableControllerValidationStrategy
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

    /**
     * 注入 一个默认兜底策略 !!!
     */
    @Bean
    fun factoryBasedMethodArgumentMessageConverterForCrudConfigurer(): FactoryBasedMethodArgumentMessageConverterConfigurer {
        return FactoryBasedMethodArgumentMessageConverterConfigurer {
            // fallback ...
            it.registerHandlers(
                DefaultFactoryBasedJsonHMAMessageConverterHandlerProvider(
                    Param::class.java,
                    // 必须不能是接口 !!
                    { it is Param },
                    object :
                        FactoryBasedJsonHMAMessageConverterHandler {
                        override fun get(value: HttpMessageContext): Any {
                            return simpleHandlerMethodArgumentResolverHandler.messageConverter.read(
                                value.targetClass,
                                value.inputMessage
                            )
                        }

                        override fun supportsParameter(parameter: MethodParameter): Boolean {
                            return !it.javaClass.isInterface
                        }

                    }
                )
            )
        }
    }

    /**
     * crud service 的方法参数解析器 !!!
     */
    @Bean
    fun factoryBasedMethodArgumentResolver(): FactoryBasedHandlerMethodArgumentResolverConfigurer {
        return FactoryBasedHandlerMethodArgumentResolverConfigurer {
            it.addArgumentResolverHandlers(
                DefaultFactoryBasedHMArgumentResolverHandlerProvider(
                    Param::class.java,
                    simpleHandlerMethodArgumentResolverHandler,
                    simpleHandlerMethodArgumentResolverHandler.predicate
                )
            )
        }
    }

}