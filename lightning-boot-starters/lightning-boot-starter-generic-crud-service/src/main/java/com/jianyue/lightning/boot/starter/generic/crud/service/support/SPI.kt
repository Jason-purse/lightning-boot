package com.jianyue.lightning.boot.starter.generic.crud.service.support

import com.jianyue.lightning.boot.starter.util.isNull
import org.springframework.core.io.support.SpringFactoriesLoader
import org.springframework.util.ClassUtils

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 10:54
 * @Description SPI
 *
 * 屏蔽底层细节
 */
object SPI {
    /**
     * 只需要发现,不需要具体类型
     */
    fun findImplementationsNoNeedType(iface: Class<out Any>): List<Class<Any>> {
        return findImplementations(iface)
    }

    /**
     * 发现第一个实现即可
     */
    fun findImplementationNoNeedType(iface: Class<out Any>): Class<Any> {
        return findImplementationsNoNeedType(iface).let {
            if (it.isNull()) {
                throw IllegalArgumentException("can't find one implementation")
            }
            it[0]
        }
    }

    /**
     * 需要发现具体类型
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> findImplementations(iface: Class<out T>): List<Class<T>> {
        return SpringFactoriesLoader.loadFactoryNames(iface, iface.classLoader).map {
            ClassUtils.resolveClassName(it, ClassUtils.getDefaultClassLoader()) as Class<T>
        }
    }

}