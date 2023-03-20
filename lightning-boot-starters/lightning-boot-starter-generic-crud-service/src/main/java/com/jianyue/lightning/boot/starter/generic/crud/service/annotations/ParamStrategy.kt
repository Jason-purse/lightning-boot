package com.jianyue.lightning.boot.starter.generic.crud.service.annotations

import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.AbstractCrudService
import java.lang.annotation.Inherited

/**
 * 支持 通过spi注入的方式 解析方法参数 !!!
 *
 *
 * [AbstractCrudService.getParamClass] 返回的参数类如果出现ParamStrategy 注解注释,则
 * 将从类路径上进行参数 策略 解析 !!!
 *
 *
 * 如果返回的类为[com.jianyue.lightning.framework.generic.crud.abstracted.param.Param] 则无法解析 !!
 * 子类必须实现Param并设置ParamStrategy注解 !!!
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Inherited
@MustBeDocumented
annotation class ParamStrategy(val value: String = DEFAULT_STRATEGY,
                                        /**
                                         * 指定请求上面的请求参数的值的标签,作为策略的标识符 !!!
                                         */
                                        val requestParamLabel: String = DEFAULT_REQUEST_PARAM_LABEL) {
    companion object {
        const val DEFAULT_REQUEST_PARAM_LABEL = "strategyKey"
        const val DEFAULT_STRATEGY = "default"
    }
}