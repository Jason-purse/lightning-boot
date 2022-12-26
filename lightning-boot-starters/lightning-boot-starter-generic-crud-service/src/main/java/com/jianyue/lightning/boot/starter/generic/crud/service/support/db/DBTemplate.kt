package com.jianyue.lightning.boot.starter.generic.crud.service.support.db

import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport

/**
 * @date 2022/12/8
 * @time 21:32
 * @author FLJ
 * @since 2022/12/8
 *
 *
 * DBTemplate
 **/
interface DBTemplate {

    fun <T : Any> add(data: T)

    fun <T: Any> addList(data: List<T>)

    fun <T : Any> update(data: T)

    fun <T> delete(query: QuerySupport, entityClass: Class<T>)

    fun <T> deleteById(query: IDQuerySupport, entityClass: Class<T>)

    fun <T> selectById(query: IDQuerySupport, entityClass: Class<T>): T?

    fun <T> selectByComplex(query: QuerySupport, entityClass: Class<T>): List<T>

    fun <T> selectFirst(query: QuerySupport, entityClass: Class<T>): T

    fun <T> selectFirstOrNull(query: QuerySupport, entityClass: Class<T>): T?

    /**
     * 根据条件统计
     */
    fun <T> countBy(query: QuerySupport, entityClass: Class<T>): Long

}