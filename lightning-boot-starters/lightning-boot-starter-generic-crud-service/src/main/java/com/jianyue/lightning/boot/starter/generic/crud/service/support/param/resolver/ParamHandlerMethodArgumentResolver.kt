package com.jianyue.lightning.boot.starter.generic.crud.service.support.param.resolver

import com.jianyue.lightning.boot.starter.generic.crud.service.annotations.ParamStrategy
import com.jianyue.lightning.boot.starter.generic.crud.service.support.ClassPathScanningCandidateComponentScanner
import com.jianyue.lightning.boot.starter.generic.crud.service.support.controller.ControllerSupport
import com.jianyue.lightning.boot.starter.util.ElvisUtil
import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple
import com.jianyue.lightning.boot.starter.util.isNotBlank
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import com.jianyue.lightning.framework.web.method.argument.resolver.AbstractWithValidationMethodArgumentResolver
import com.jianyue.lightning.framework.web.method.argument.resolver.FirstClassSupportHandlerMethodArgumentResolver
import com.jianyue.lightning.util.JsonUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.util.ClassUtils
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.annotation.ModelFactory
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServletRequest
import kotlin.math.log

/**
 * @author FLJ
 * @date 2022/12/13
 * @time 11:21
 * @Description 实现自定义解析
 *
 * 这种基于 Param 参数类型进行解析 !!
 * FirstClassSupportHandlerMethodArgumentResolver 表示比默认的优先执行 !!!
 *
 * 支持 @RequestBody的解析 !!!!
 *
 * 如下所述,当多个controller同时使用一种Param抽象,则可能导致一个满足条件的添加ParamStrategy注解会"不正确"的注入到controller的参数中 !!
 * 但是,本身这个参数实现,确实抽象的实现类,所以这可能需要开发者在业务使用上进行 细致的隔离,例如
 *                       Param
 *                      /      \
 *     organizationParam        orderParam
 *
 *          /       \
 *        Simple     Complex
 *
 * 而不是所有的实现都继承于Param,这样业务上估计也无法分离开 !!! 并且有多个相同父类的子类实现相同的策略将无法正常进行参数解析 !!!
 *
 */
class ParamHandlerMethodArgumentResolver(
        private val baseScanPackage:
        Set<String>) : AbstractWithValidationMethodArgumentResolver(), FirstClassSupportHandlerMethodArgumentResolver {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)


    private val paramClassList = mutableListOf<Class<out Param>>()

    private val paramResolveCache: ConcurrentHashMap<MethodParameter, ConcurrentHashMap<String, Class<out Param>>> = ConcurrentHashMap()

    init {
        initParamClass()
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType.isInterface &&
                Param::class.java.isAssignableFrom(parameter.parameterType) &&
                ControllerSupport.paramClassState.get()?.first != null;
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
        var attribute: Any? = null;
        ControllerSupport.paramClassState.get()!!.run {
            var bindingResult: BindingResult? = null;
            val name = ModelFactory.getNameForParameter(parameter);
            var subClass = first
            if (subClass.isInterface) {
                subClass = getForCacheOrResolveImmediately(parameter, subClass, webRequest)
            }

            logger.info("resolve target class is {}", subClass)
            if (parameter.hasParameterAnnotation(RequestBody::class.java)) {
                try {
                    webRequest
                            .getNativeRequest(HttpServletRequest::class.java)!!
                            .run {
                                attribute = JsonUtil.getDefaultJsonUtil().fromJson(inputStream, subClass)
                            }
                } catch (e: BindException) {
                    if (isBindExceptionRequired(parameter)) {
                        throw e;
                    }
                    attribute = e.target
                    bindingResult = e.bindingResult

                    logger.info("from json resolve failure,because {}", e.message)
                }

                if (attribute != null) {
                    validationAttribute(parameter, subClass, mavContainer, webRequest, binderFactory, attribute, name, bindingResult, false)
                }

            } else {
                attribute = fromRequestParameters(parameter, subClass, mavContainer, webRequest, binderFactory)
            }
        }

        logger.info("resolve attribute is {}", attribute)
        return attribute;
    }


    override fun getRequestValueForAttribute(attributeName: String, request: NativeWebRequest): String {
        val requestValueForAttribute = super.getRequestValueForAttribute(attributeName, request)
        // 仅仅是为了,进入下一个方法 createAttributeFromRequestValue
        // 既然是Param 就是一个复杂对象, 这一般是对简单对象进行处理 !!
        return ElvisUtil.stringElvis(requestValueForAttribute, "");
    }


    private fun getForCacheOrResolveImmediately(parameter: MethodParameter, parentType: Class<out Param>, request: NativeWebRequest): Class<out Param> {
        val strategyMaps = paramResolveCache
                .computeIfAbsent(parameter) {
                    ConcurrentHashMap()
                }
        if (strategyMaps.isEmpty()) {
            return fromDirectResolve(parentType, request, strategyMaps)
        } else {
            // 有的情况下 !!
            return parentType.run {
                for ((key, value) in strategyMaps) {
                    if (request.getParameter(key).isNotBlank()) {
                        return@run value
                    }
                }

                // 里面没有,继续走之前的逻辑,也就是策略并没有完全解析完毕 !!!
                return@run fromDirectResolve(parentType, request, strategyMaps)
            }
        }
    }

    private fun fromDirectResolve(parentType: Class<out Param>, request: NativeWebRequest, strategyMaps: ConcurrentHashMap<String, Class<out Param>>): Class<out Param> {
        val tuple: Tuple<String, Class<out Param>> = parentType.run {
            parentType.getImplementationClassByStrategy(paramClassList, request)
        }

        strategyMaps[tuple.first] = tuple.second

        return tuple.second;
    }

    private fun Class<out Param>.getImplementationClassByStrategy(list: List<Class<out Param>>, request: NativeWebRequest): Tuple<String, Class<out Param>> {
        var defaultStrategyClass: Class<out Param>? = null;
        var tempStrategyClass: Tuple<String, Class<out Param>>? = null;
        var hasStrategy = false
        for (clazz in list) {

            if (isAssignableFrom(clazz)) {

                // 判断注解上的value 和 请求中的参数是否一致
                AnnotationUtils.getAnnotation(clazz, ParamStrategy::class.java)?.let {

                    val requestParamLabel = ElvisUtil.stringElvis(it.requestParamLabel, ParamStrategy.DEFAULT_REQUEST_PARAM_LABEL);
                    request.getParameter(requestParamLabel).run {

                        if(isNotBlank()) {
                            if (!hasStrategy) {
                                hasStrategy = true;
                            }

                            if (this == it.value) {
                                if (tempStrategyClass == null) {
                                    tempStrategyClass = Tuple(it.value, clazz);
                                } else {
                                    throw IllegalArgumentException("Multiple classes that match the strategy were found,[${it.value} -> ${clazz.simpleName},${tempStrategyClass!!.first} -> ${tempStrategyClass!!.second.simpleName}],Please use Param's subclass interface abstraction wisely !!!")
                                }
                            }
                        }

                        if (!hasStrategy) {
                            // 找默认值 !!
                            if (it.value == ParamStrategy.DEFAULT_STRATEGY) {
                                if (defaultStrategyClass == null) {
                                    defaultStrategyClass = clazz
                                } else {
                                    throw IllegalArgumentException("Multiple classes that match the strategy were found,[${clazz.simpleName},${defaultStrategyClass!!.simpleName}],Please use Param's subclass interface abstraction wisely !!!")
                                }
                            }
                        }

                    }

                }
            }
        }

        if (hasStrategy) {
            if(tempStrategyClass == null) {
                throw IllegalArgumentException("cant find sub type of $simpleName !!!")
            }
            return tempStrategyClass!!
        }

        if (defaultStrategyClass != null) {
            return Tuple(ParamStrategy.DEFAULT_STRATEGY, defaultStrategyClass)
        }

        throw IllegalAccessException("can't find sub type of $simpleName !!!")
    }


    @Suppress("UNCHECKED_CAST")
    private fun initParamClass() {
        ClassPathScanningCandidateComponentScanner(baseScanPackage, Param::class.java, ParamStrategy::class.java)
                .apply {
                    for (packageName in baseScanPackage) {
                        val components = findCandidateComponents(packageName)
                        for (findCandidateComponent in components) {
                            val clazz = ClassUtils.forName(findCandidateComponent.beanClassName!!, javaClass.classLoader) as Class<out Param>
                            paramClassList.add(clazz);
                        }
                    }

                    // 也就是这里无法验证,需要开发者准守规范 !!
                    // 否则一个满足条件的另一个Param实现可能会注入到不该注入的地方上去 !!!(在符合单个策略条件的情况下) ...
                    // Param -> MyParam -> OrganizationParamService<Param> !!!!
//                    val values: MutableList<MutableSet<String>> = mutableListOf()
//                    val errorIndexs: MutableSet<Int> = mutableSetOf()
//                    paramClassMap.onEachIndexed { index, (_, value) ->
//                        {
//                            val strategyList: MutableSet<String> = mutableSetOf<String>()
//                            for ((_, clazz) in value.withIndex()) {
//                                if (!strategyList.add(AnnotationUtils.findAnnotation(clazz, ParamStrategy::class.java)!!.value)) {
//                                    errorIndexs.add(index);
//                                }
//                            }
//                            values.add(strategyList);
//                        }
//
//                        if (errorIndexs.size > 0) {
//                            // 存在错误 !!!
//                            for (errorIndex in errorIndexs) {
//                                logger.warn("There is an error in the parameter policy, which contains the following wrong policy: ${values[errorIndex]} \n")
//                            }
//                        }
//                    }
                }
    }

}