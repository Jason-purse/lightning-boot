package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

/**
 * @author FLJ
 * @date 2022/12/8
 * @time 17:14
 * @Description 查询信息
 */
abstract class AbstractQueryInfo<T>(private val nativeQuery: T): QueryInfo<T> {

    // native Query
    override fun getNativeQuery(): T {
        return nativeQuery;
    }


}