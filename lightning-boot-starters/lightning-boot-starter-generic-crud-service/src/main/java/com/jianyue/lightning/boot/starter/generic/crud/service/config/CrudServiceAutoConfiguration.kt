package com.jianyue.lightning.boot.starter.generic.crud.service.config

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.ControllerSupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.JpaDbTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.MongoDbTemplate
import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpInputMessage
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.lang.reflect.Type

@ConditionalOnClass(WebMvcConfigurer::class)
@Import(MongoDbTemplate::class, JpaDbTemplate::class, AopConfig::class)
@AutoConfigureAfter(value = [MongoDataAutoConfiguration::class, JpaRepositoriesAutoConfiguration::class])
class CrudServiceAutoConfiguration : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        // 为了参数解析
        resolvers.add(GenericCRUDModelAttributeMethodProcessor());
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {

        val messageConverter = converters.first { it is MappingJackson2HttpMessageConverter } as MappingJackson2HttpMessageConverter
        // 继承 mvc jackson 的配置选项
        // JacksonHttpMessageConvertersConfiguration
        converters.add(1, object : MappingJackson2HttpMessageConverter(messageConverter.objectMapper) {
            override fun getJavaType(type: Type, contextClass: Class<*>?): JavaType {
                val paramClass = ControllerSupport.paramClassState.get()
                if (type is Class<*> && Param::class.java.isAssignableFrom(type)) {
                    if (paramClass != null) {
                        val typeFactory = objectMapper.typeFactory
                        return typeFactory.constructType(paramClass.first)
                    }
                }
                // 否则默认处理 ..
                return super.getJavaType(type, contextClass)
            }

            override fun read(type: Type, contextClass: Class<*>?, inputMessage: HttpInputMessage): Any {
                val paramClassTuple = ControllerSupport.paramClassState.get()
                return super.read(type, contextClass, inputMessage).apply {
                    @Suppress("UNCHECKED_CAST")
                    ControllerSupport.paramClassState.set(paramClassTuple?.second as? Tuple<Class<*>, Any>)
                }

            }
        })
    }
}