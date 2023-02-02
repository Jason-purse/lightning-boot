package com.jianyue.lightning.framework.generic.crud.abstracted.param

/**
 *
 * 这里的 * 不想要在使用的时候,还需要提供S类型 ...
 * 这样,仅仅只需要asNativeObject<T>()即可, 或者基于自动推断,或许不需要T ..
 * <pre>
 *     val result: Param =  value.asNativeObject()
 * <pre>
 *
 * @throws ClassCastException
 */
inline fun <reified T> AsSupport<*>.asNativeObject(): T {
    return this as T
}