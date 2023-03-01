package com.jianyue.lightning.boot.starter.generic.crud.service.config


import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.DBTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.NONE
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.StrategyGroup
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.StrategyGroupSupport
import com.jianyue.lightning.boot.starter.util.isNotNull

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.aspectj.lang.annotation.Aspect
import org.springframework.aop.Advisor
import org.springframework.aop.ClassFilter
import org.springframework.aop.MethodMatcher
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.aop.support.annotation.AnnotationMethodMatcher
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.util.ConcurrentReferenceHashMap
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import java.lang.reflect.Method
import kotlin.reflect.full.isSubclassOf


@Configuration
@EnableAspectJAutoProxy
@Aspect
@ConditionalOnBean(value = [DBTemplate::class])
class AopConfig {
    companion object {
        private val validationGroupCache = ConcurrentReferenceHashMap<Method, Class<out StrategyGroup>>();
    }


    @Bean
    fun validationAdvisor(): Advisor {
        return DefaultPointcutAdvisor(
            object : org.springframework.aop.Pointcut {
                private val methodPreMatcher = AnnotationMethodMatcher(RequestMapping::class.java, true);
                private val otherMethodPreMatcher = AnnotationMethodMatcher(ValidationAnnotation::class.java, true);
                override fun getClassFilter(): ClassFilter {
//                    return AnnotationClassFilter(Controller::class.java, true)
                    return ClassFilter.TRUE;
                }

                override fun getMethodMatcher(): MethodMatcher {

                    return object : MethodMatcher {
                        override fun matches(method: Method, targetClass: Class<*>): Boolean {
                            return methodPreMatcher.matches(
                                method,
                                targetClass
                            ) && otherMethodPreMatcher.matches(method, targetClass)
                        }

                        override fun matches(method: Method, targetClass: Class<*>, vararg args: Any?): Boolean {
                            TODO("NOT IMPLEMENTATION")
                        }

                        override fun isRuntime(): Boolean {
                            return false;
                        }
                    }
                }

            },
            MethodInterceptor {
                val oldValidationGroup = validationGroupSet(it)
                val result = it.proceed()
                validationGroupRemove(oldValidationGroup)
                result
            }
        )
    }

    //    TODO
    private fun inspectParameterAnnotations(method: Method, index: Int): Boolean {
        if (index == -1) {
            for (parameterAnnotation in method.parameterAnnotations) {
                if (inspectParameterAnnotation(parameterAnnotation)) {
                    return true;
                }
            }
        } else {
            return inspectParameterAnnotation(method.parameterAnnotations[index])
        }

        return false;
    }


    private fun inspectParameterAnnotation(annotation: Array<Annotation>): Boolean {
        annotation.let {
            for (clazz in it) {
                // 将它作为一个元注解的载体,向上解析
                AnnotationUtils.findAnnotation(
                    clazz.annotationClass.java,
                    ValidationAnnotation::class.java
                ).apply {
                    return it.isNotNull()
                }
            }
            return false
        }
    }


    private fun validationGroupSet(jointpoint: MethodInvocation): Class<out StrategyGroup>? {

        // 如果存在
        validationGroupCache[jointpoint.method]?.let {
            return StrategyGroupSupport.setStrategyGroupAndReturnOld(it)
        }

        // 否则计算
        jointpoint.method.parameterAnnotations.let {
            // 按道理来说,参数必然不为空

            // 可以细节化,给定到每一个参数
            // TODO()
            if (it.isNotEmpty()) {
                for (annotations in it) {
                    for (annotation in annotations) {
                        AnnotationUtils.findAnnotation(annotation.annotationClass.java, Validated::class.java)
                            ?.run {
                                if (this.value.isNotEmpty()) {
                                    if (this.value[0].isSubclassOf(StrategyGroup::class)) {

                                        @Suppress("UNCHECKED_CAST")
                                        (this.value[0].java as Class<out StrategyGroup>).apply {
                                            validationGroupCache[jointpoint.method] = this
                                            return StrategyGroupSupport.setStrategyGroupAndReturnOld(this)
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }

        return StrategyGroupSupport.setStrategyGroupAndReturnOld(NONE::class.java)
    }

    private fun validationGroupRemove(validationGroup: Class<out StrategyGroup>?) {
        StrategyGroupSupport.setStrategyGroupAndReturnOld(validationGroup)
    }
}