package com.jianyue.lightning.boot.starter.generic.crud.service.support.db

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
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

    fun <T : Entity> add(data: T)

    fun <T: Entity> addList(data: List<T>)

    fun <T : Entity> update(data: T)

    fun <T : Entity> delete(query: QuerySupport, entityClass: Class<T>)

    fun <T : Entity> deleteById(query: IDQuerySupport, entityClass: Class<T>)

    fun <T : Entity> selectById(query: IDQuerySupport, entityClass: Class<T>): T?

    fun <T : Entity> selectByComplex(query: QuerySupport, entityClass: Class<T>): List<T>

    fun <T : Entity> selectFirst(query: QuerySupport, entityClass: Class<T>): T

    fun <T : Entity> selectFirstOrNull(query: QuerySupport, entityClass: Class<T>): T?

    /**
     * 根据条件统计
     */
    fun <T: Entity> countBy(query: QuerySupport, entityClass: Class<T>): Long

}