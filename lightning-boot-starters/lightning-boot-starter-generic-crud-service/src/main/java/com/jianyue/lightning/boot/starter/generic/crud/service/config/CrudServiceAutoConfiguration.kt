package com.jianyue.lightning.boot.starter.generic.crud.service.config

import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.EnableControllerValidationStrategy
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.JpaDbTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.MongoDbTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver.ParamHandlerMethodArgumentResolver
import com.jianyue.lightning.boot.starter.util.ElvisUtil
import com.jianyue.lightning.framework.web.config.LightningWebProperties
import com.jianyue.lightning.framework.web.util.ClassUtil
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 配置generic crud service 的相关组件 !!!
 */
@ConditionalOnClass(WebMvcConfigurer::class)
@EnableControllerValidationStrategy
@Import(MongoDbTemplate::class, JpaDbTemplate::class)
@EnableConfigurationProperties(GenericCrudProperties::class)
@AutoConfigureAfter(value = [MongoDataAutoConfiguration::class, JpaRepositoriesAutoConfiguration::class])
class CrudServiceAutoConfiguration(private val properties: GenericCrudProperties) : WebMvcConfigurer {

    /**
     * param handler method argument resolver !!!
     */
    @Bean
    fun paramHandlerMethodArgumentResolver(webProperties: LightningWebProperties): ParamHandlerMethodArgumentResolver {
        val paramScanPages = ElvisUtil.acquireNotNullList_Empty(properties.paramScanPages).run {
            HashSet(this).apply {
                add(ClassUtil.getPackageNameForClass(webProperties.mainApplicationClass))
            }
        }
        return ParamHandlerMethodArgumentResolver(paramScanPages);
    }
}