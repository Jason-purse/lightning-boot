package com.jianyue.lightning.boot.starter.generic.crud.service.config


import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.ValidationAnnotation
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.DBTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.NONE
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.StrategyGroup
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.StrategyGroupSupport
import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple
import com.jianyue.lightning.boot.starter.util.isNotNull

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.aop.Advisor
import org.springframework.aop.ClassFilter
import org.springframework.aop.MethodMatcher
import org.springframework.aop.support.ClassFilters
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.aop.support.RootClassFilter
import org.springframework.aop.support.annotation.AnnotationClassFilter
import org.springframework.aop.support.annotation.AnnotationMethodMatcher
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Controller
import org.springframework.util.ConcurrentReferenceHashMap
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import kotlin.reflect.full.isSubclassOf

/**
 * @author FLJ
 * @date 2023/3/2
 * @time 11:22
 * @Description 实现 控制器上的验证  - 基于策略组进行 参数校验 !!!
 *
 * 通过导入器导入 !!!!
 */
@AutoConfiguration
@Configuration
@EnableAspectJAutoProxy
@Aspect
@ConditionalOnBean(value = [DBTemplate::class])
class ControllerValidationAopAspectConfiguration {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    companion object {
        /**
         * 验证分组缓存 !!!
         */
        private val validationGroupCache = ConcurrentReferenceHashMap<Method, Class<out StrategyGroup>>();
    }

    init {
        logger.info("init controller validation aop aspect configuration !!!")
    }


    @Bean
    fun validationAdvisor(): Advisor {
        return DefaultPointcutAdvisor(
            object : org.springframework.aop.Pointcut {
                private val methodPreMatcher = AnnotationMethodMatcher(RequestMapping::class.java, true);
                private val otherMethodPreMatcher =
                    AnnotationMethodMatcher(ValidationAnnotation::class.java, true);

                override fun getClassFilter(): ClassFilter {
                    return ClassFilters.intersection(
                        RootClassFilter(AbstractGenericController::class.java),
                        AnnotationClassFilter(Controller::class.java, true)
                    )
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
                val groupTuple = validationGroupSet(it)
                logger.info("set validation group from {} to {} !!", groupTuple.second,groupTuple.first)
                val result = it.proceed()
                validationGroupRemove(groupTuple.second)
                result
            }
        )
    }


    private fun validationGroupSet(jointpoint: MethodInvocation): Tuple<Class<out StrategyGroup>?, Class<out StrategyGroup>?> {

        // 如果存在
        validationGroupCache[jointpoint.method]?.let {
            val old = StrategyGroupSupport.setStrategyGroupAndReturnOld(it)
            return Tuple(it, old)
        }
        val currentTargetObject = jointpoint.`this`!!
        getValidationAnnotation(currentTargetObject.javaClass, jointpoint).let {
            if (it.isNotNull()) {
                return Tuple(it, StrategyGroupSupport.setStrategyGroupAndReturnOld(it))
            }
            getValidationAnnotation(jointpoint.method, jointpoint).let { ele ->
                if (ele.isNotNull()) {
                    return Tuple(ele, StrategyGroupSupport.setStrategyGroupAndReturnOld(ele));
                }
            }
        }

        return Tuple(NONE::class.java, StrategyGroupSupport.setStrategyGroupAndReturnOld(NONE::class.java))
    }

    private fun getValidationAnnotation(
        annotationElement: AnnotatedElement,
        jointpoint: MethodInvocation
    ): Class<out StrategyGroup>? {
        AnnotationUtils.findAnnotation(annotationElement, Validated::class.java)?.let {
            if (it.value.isNotEmpty()) {
                if (it.value[0].isSubclassOf(StrategyGroup::class)) {

                    @Suppress("UNCHECKED_CAST")
                    (it.value[0].java as Class<out StrategyGroup>).apply {
                        validationGroupCache[jointpoint.method] = this
                        return this@apply
                    }
                }
            }
        }
        return null
    }

    private fun validationGroupRemove(validationGroup: Class<out StrategyGroup>?) {
        StrategyGroupSupport.setStrategyGroupAndReturnOld(validationGroup)
    }
}