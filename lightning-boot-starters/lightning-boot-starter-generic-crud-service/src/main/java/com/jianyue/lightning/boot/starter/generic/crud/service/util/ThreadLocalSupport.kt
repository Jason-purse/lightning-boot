package com.jianyue.lightning.boot.starter.generic.crud.service.util

import com.jianyue.lightning.boot.starter.util.isNull


/**
 * @date 2022/12/10
 * @time 14:02
 * @author FLJ
 * @since 2022/12/10
 *
 *
 * 线程变量存放支持,对null值 敏感 !!!
 **/
interface ThreadLocalSupport<T> {

    fun set(t: T?): T?

    fun get(): T?

    fun setAndReturnOld(t: T?): T? {
        return get().apply {
            set(t);
        }
    }

    fun removeAndReturnOld(): T? {
        return get().apply {
            remove()
        }
    }

    fun remove(): T?


    companion object {


        fun <T> of(initValue: T): ThreadLocalSupport<T> {
            return DefaultThreadLocalSupport(initValue)
        }

        fun <T> of(): ThreadLocalSupport<T> {
            return DefaultThreadLocalSupport()
        }
    }
}

private class DefaultThreadLocalSupport<T> constructor(initValue: T?) : ThreadLocalSupport<T> {

    private val threadLocal: ThreadLocal<T> = object : ThreadLocal<T>() {
        override fun initialValue(): T? {
            return initValue;
        }
    }

    constructor() : this(null)


    override fun set(t: T?): T? {
        threadLocal.get().let {
            if (t == null) {
                threadLocal.remove()
            }
            threadLocal.set(t);
            return it
        }
    }

    override fun get(): T? {
        threadLocal.get().let {
            if (it.isNull()) {
                threadLocal.remove()
            }
            return it
        }
    }

    override fun remove(): T? {
        threadLocal.get().let {
            threadLocal.remove()
            return it
        }
    }
}