package com.safone.order.service.model.order.verification.support.converters

import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.Converter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import org.springframework.core.ResolvableType

/**
 * @date 2022/12/8
 * @time 21:03
 * @author FLJ
 * @since 2022/12/8
 *
 *
 * query 参数转换器,一般param 会接收前台的 query数据,实现了这种接口的param 能够直接转换为Query,进行数据库条件查询
 *
 *
 * 此Converter 可以结合
 * @see com.jianyue.lightning.boot.starter.generic.crud.service.config.AopConfig 来基于线程安全的方式,获取当前存储的有关参数的校验组
 * @see com.jianyue.lightning.boot.starter.generic.crud.service.support.strategy.CrudStrategySupport 来根据不同校验组形成,不同的Query ..
 *
 * 同样,此类可以使用一个接口P 作为许多子类型的处理的ConverterAdapter(或者代理 / 包装器模式,然后代理到具体子类的转换器实现) ...
 * 减少代码修改范围 ...
 **/
interface QueryConverter<P> : Converter<P, QuerySupport> {

    /**
     * 默认java assignableFrom 语义
     */
    override fun support(param: Any): Boolean {
        return ResolvableType.forType(getSourceClass()).isInstance(param)
    }

    override fun getTargetClass(): Class<QuerySupport> {
        return QuerySupport::class.java
    }

}



