package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters

import java.lang.reflect.Type

/**
 * @date 2022/12/10
 * @time 8:24
 * @author FLJ
 * @since 2022/12/10
 *
 *
 * 转换器 ..  S,T (原始对象 ,目标对象)
 *
 * 1. 初版使用 Class泛型类,但是无法完全判断完全(例如不可能产生Collection<String>等新的类型) ..
 * 2. 使用Type
 *
 * 3. 当S 是一个集合泛型类,请使用List
 * @see com.jianyue.lightning.boot.starter.generic.crud.service.support.service.AbstractCrudService 或者重新实现自己的 CrudService ...
 *
 *
 * 对于需要手动释放一些资源的,被spring管理可以实现DisposableBean 接口,否则 ReleaseAwaredConverter 可以实现 ..
 **/
interface Converter<S,T> : ConverterSupport {

    /**
     * 是否支持转换
     */
    fun support(param: Any): Boolean


    /**
     * 由于 kotlin的使用点 差异,导致有可能拿到的converter 是一个S == Noting的情况(例如使用 out(生产者的情况下),只能生产不能消费)
     * 借助此方法实现,一个伪强转 ..
     */
    @Suppress("UNCHECKED_CAST")
    fun convertForAny(param: Any): T? {
        return convert(param as S);
    }

    /**
     * 转换过程
     */
    fun convert(param: S): T?


    fun getSourceClass(): Type

    fun getTargetClass(): Type
}


