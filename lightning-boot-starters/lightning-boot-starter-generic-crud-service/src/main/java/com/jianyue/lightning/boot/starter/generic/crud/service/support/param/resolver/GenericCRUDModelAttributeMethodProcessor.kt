package com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver

import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.AbstractGenericController
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.ControllerSupport
import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import org.springframework.beans.BeanUtils
import org.springframework.core.MethodParameter
import org.springframework.util.Assert
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor
import java.lang.reflect.Constructor

/**
 * @author FLJ
 * @date 2022/12/13
 * @time 11:21
 * @Description 实现自定义解析
 */
class GenericCRUDModelAttributeMethodProcessor : ServletModelAttributeMethodProcessor(true) {

    private fun getPrimaryConstructor(paramClass: Class<*>): Constructor<*> {
        var ctor: Constructor<*>? = BeanUtils.findPrimaryConstructor(paramClass)
        if (ctor == null) {
            val ctors: Array<Constructor<*>> = paramClass.constructors
            ctor = if (ctors.size == 1) {
                ctors[0]
            } else {
                try {
                    paramClass.getDeclaredConstructor()
                } catch (ex: NoSuchMethodException) {
                    throw IllegalStateException("No primary or default constructor found for $paramClass", ex)
                }
            }
        }
        return ctor!!
    }

    override fun getRequestValueForAttribute(attributeName: String, request: NativeWebRequest): String {
        // 仅仅是为了,进入下一个方法 createAttributeFromRequestValue
        return "";
    }

    override fun createAttributeFromRequestValue(
        sourceValue: String,
        attributeName: String,
        parameter: MethodParameter,
        binderFactory: WebDataBinderFactory,
        request: NativeWebRequest
    ): Any? {
        if (Param::class.java.isAssignableFrom(parameter.parameterType)) {
            // 根据参数获取
            parameter.method?.let {
                // 实现通用 ... MethodProcessor ..
                if (AbstractGenericController::class.java.isAssignableFrom(it.declaringClass)) {
                    val paramClassTuple = ControllerSupport.paramClassState.get()
                    @Suppress("UNCHECKED_CAST")
                    // 将之前的param 放回去 .

                    ControllerSupport.paramClassState.set(paramClassTuple?.second as? Tuple<Class<*>, Any>)
                    if (paramClassTuple != null) {
                        // never impossible
                        return BeanUtils.instantiateClass(getPrimaryConstructor(paramClassTuple.first).apply {
                            Assert.isTrue(
                                parameterCount == 0,
                                "must provide a parameter-free constructor !!!"
                            )
                        })
                    }
                }
            }
        }
        return super.createAttributeFromRequestValue(sourceValue, attributeName, parameter, binderFactory, request)
    }
}