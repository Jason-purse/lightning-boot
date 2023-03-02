package com.jianyue.lightning.boot.starter.generic.crud.service.config

import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.EnableControllerValidationStrategy
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.JpaDbTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.MongoDbTemplate
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerFactoryConfigurer
import com.jianyue.lightning.framework.web.method.argument.resolver.FactoryBasedHandlerMethodArgumentResolver
import com.jianyue.lightning.framework.web.method.argument.resolver.HandlerMethodArgumentResolverHandlerProvider
import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.SimpleForGenericCrudHandlerMethodArgumentResolverHandler
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 主要负责通用crud 的{@link Param}的实例化方式 ...
 *
 * 当然也支持其他基于工厂策略的抽象参数实例化 ... 不限于 Param 参数 ...
 *
 * 主要使用形式就是,接口上是一个抽象类,需要自定义工厂实现,来进行具体的参数构造 ....
 *
 * 要支持其他类型的工厂 参数解析,通过实现 FactoryBasedHandlerFactoryConfigurer 进行自定义扩展 ..
 * 同样可以修改已有内置的对通用crud服务支持的 JsonUtil,去实现特定的类型反序列化或者序列化支持 ...
 */
@ConditionalOnClass(WebMvcConfigurer::class)
@EnableControllerValidationStrategy
@Import(MongoDbTemplate::class, JpaDbTemplate::class)
@AutoConfigureAfter(value = [MongoDataAutoConfiguration::class, JpaRepositoriesAutoConfiguration::class])
class CrudServiceAutoConfiguration : WebMvcConfigurer {
    private val simpleHandlerMethodArgumentResolverHandler =
        SimpleForGenericCrudHandlerMethodArgumentResolverHandler()


    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        // 直接将 参数解析器 处理器 放入工厂中 !!!!
        // 追加到工厂中 !!!
        resolvers.filterIsInstance<FactoryBasedHandlerMethodArgumentResolver>()
            .forEach {
                it.addArgumentResolverHandlers(// 针对 Param java类的处理 ...
                    HandlerMethodArgumentResolverHandlerProvider(
                        Param::class.java,
                        simpleHandlerMethodArgumentResolverHandler,
                        simpleHandlerMethodArgumentResolverHandler.predicate
                    )
                )
            }
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // 继承 mvc jackson 的配置选项
        // JacksonHttpMessageConvertersConfiguration
        converters.add(1, simpleHandlerMethodArgumentResolverHandler.messageConverter)
    }
}