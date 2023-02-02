package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

/**
 * @author FLJ
 * @date 2022/12/8
 * @time 17:12
 * @Description 获取具体的查询条件,例如MongoQuery / jdbc Query
 */
interface QueryInfo<T> {

    fun getNativeQuery(): T
}

