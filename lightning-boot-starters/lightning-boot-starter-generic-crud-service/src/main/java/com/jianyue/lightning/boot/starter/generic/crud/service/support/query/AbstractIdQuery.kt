package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

/**
 * @date 2022/12/8
 * @time 20:42
 * @author FLJ
 * @since 2022/12/8
 *
 * id query 抽象实现
 **/
abstract class AbstractIdQuery<T,QueryInfo>(private val id: T,private val idClass: Class<*>) : IdQuery<T, QueryInfo> {
    override fun getId(): T {
        return id;
    }

    override fun getIdClass(): Class<*> {
        return idClass;
    }
}
