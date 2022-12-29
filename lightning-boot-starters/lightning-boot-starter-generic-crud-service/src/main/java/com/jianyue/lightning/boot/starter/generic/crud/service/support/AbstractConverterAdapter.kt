package com.jianyue.lightning.boot.starter.generic.crud.service.support


import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.Converter
import com.jianyue.lightning.boot.starter.util.ClassUtil
import com.jianyue.lightning.boot.starter.util.isNotNull
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.ReleaseAwaredConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.ResolvableType
import org.springframework.util.ConcurrentReferenceHashMap
import java.lang.reflect.Type
import java.util.*

/**
 * @date 2022/12/10
 * @time 9:34
 * @author FLJ
 * @since 2022/12/10
 *
 * QueryConverter Delegate
 *
 * 子类实现,作为Bean来说,它不应该作为Converter的候选者
 **/

abstract class AbstractConverterAdapter<S, T> : Converter<S, T>, ApplicationContextAware,DisposableBean {

    companion object {
        // Type with Type relationShip
        // 判断一个子类有多少父类(可通性) - covariant(逆变)
        private val classRSMap: MutableMap<Type, MutableMap<Type, Boolean>> = ConcurrentReferenceHashMap()
    }

    private var converters: MutableList<Converter<S, T>> = mutableListOf();

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)


    override fun setApplicationContext(ctx: ApplicationContext) {
        // converter adapter need it
        val beans = ctx.getBeansOfType<Converter<S, T>>(includeNonSingletons = false, allowEagerInit = true)
        // 如果只有一个
        if (beans.size == 1 && beans.values.first() == this) {
            converters = Collections.emptyList()
        } else {
            // exclude itself
            val beansCandidate = beans.values.toList().filter { it != this }

            for (converter in beansCandidate) {

                // 校验工作变得更加棘手
                // 必然是Converter ..
                if (assertClassHierarchy(
                        ResolvableType.forClassWithGenerics(
                            AbstractConverterAdapter::class.java,
                            ResolvableType.forType(getSourceClass()),
                            ResolvableType.forType(getTargetClass())
                        ).type,
                        ResolvableType.forClassWithGenerics(
                            Converter::class.java,
                            ResolvableType.forType(converter.getSourceClass()),
                            ResolvableType.forType(converter.getTargetClass())
                        ).type
                    )
                ) {
                    converters.add(converter);
                }
            }

        }

    }

    // we need cache assert result for a long time ...
    private fun assertClassHierarchy(parent: Type, children: Type): Boolean {
        return classRSMap
            .computeIfAbsent(children) {
                ConcurrentReferenceHashMap()
            }.toMutableMap()
            .computeIfAbsent(parent) {
                ClassUtil.logicCovariantAssert(parent, children, Converter::class.java)
            }
    }


    fun getConverters(): List<Converter<S, T>> {
        return Collections.unmodifiableList(converters)
    }


    fun addConverters(vararg converters: Converter<S, T>) {
        for (converter in converters) {
            this.converters.add(converter)
        }
    }

    fun addConverter(index: Int, converter: Converter<S, T>) {
        this.converters.add(index, converter)
    }

    @Suppress("UNCHECKED_CAST")
    fun noSafeAddConverters(vararg converters: Converter<*, *>) {
        for (converter in converters) {
            if (logicAssignableFrom(converter)
            ) {
                this.converters.add(converter as Converter<S, T>)
            } else {
                // 类型存在问题 ...
                throw IllegalArgumentException("converter logic assignableFrom assert fail,please use correct type converter and register !!!")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun noSafeAddConverter(index: Int, converter: Converter<*, *>) {
        if (logicAssignableFrom(converter)
        ) {
            this.converters.add(index, converter as Converter<S, T>)
        } else {
            // 类型存在问题 ...
            throw IllegalArgumentException("converter logic assignableFrom assert fail,please use correct type converter and register !!!")
        }
    }

    private fun logicAssignableFrom(converter: Converter<*, *>) =
        ClassUtil.logicCovariantAssert(
            ResolvableType.forClassWithGenerics(
                AbstractConverterAdapter::class.java,
                ResolvableType.forType(getSourceClass()),
                ResolvableType.forType(getTargetClass())
            ).type,
            ResolvableType.forClassWithGenerics(
                Converter::class.java,
                ResolvableType.forType(converter.getSourceClass()),
                ResolvableType.forType(converter.getTargetClass())
            ).type, Converter::class.java
        )


    override fun support(param: Any): Boolean {
        for (converter in converters) {
            // Because there is covariance ,we need assert param is converter of sourceClass of instance .
            if (assertClassHierarchy(converter.getSourceClass(), org.springframework.util.ClassUtils.getUserClass(param::class.java))) {
                // type equals, continue check support ..
                if (converter.support(param)) {
                    return true
                }
            }
        }
        return false
    }


    override fun convert(param: S): T? {
        for (converter in converters) {
            // 如果支持但是,转换出来时 null,则继续转换
            val convert = converter.convert(param)
            if (convert.isNotNull()) {
                return convert
            }
        }
        return null
    }

    override fun destroy() {
        // 有一些直接spring 依赖注入,有一些手动加入的 ...
        for (converter in converters) {
            if (converter is ReleaseAwaredConverter) {
                converter.release()
            }
        }

        converters.clear()
    }


}

/**
 * @author FLJ
 * @date 2022/12/10
 * @time 14:53
 * @Description 默认通用的模板实现
 */
open class DefaultGenericConverterAdapter<S, T>(private val sourceClass: Type, private val targetClass: Type) :
    AbstractConverterAdapter<S, T>() {

    companion object {

        /**
         * 通过类处理
         */
        inline fun <reified S, reified T> of(): DefaultGenericConverterAdapter<S, T> {
            return DefaultGenericConverterAdapter(S::class.java, T::class.java)
        }

        /**
         * java code, class / parameterizedType
         */
        fun <S, T> of(sourceClass: Type, targetClass: Type): DefaultGenericConverterAdapter<S, T> {
            return DefaultGenericConverterAdapter(sourceClass, targetClass)
        }
    }


    override fun getSourceClass(): Type {
        return this.sourceClass;
    }

    override fun getTargetClass(): Type {
        return this.targetClass;
    }
}
