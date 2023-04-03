package com.jianyue.lightning.boot.starter.generic.crud.service.support.db

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.Serializable

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

    fun <T : Entity<out Serializable>> add(data: T)

    fun <T: Entity<out Serializable>> addList(data: List<T>)

    fun <T: Entity<out Serializable>> saveList(data: List<T>)

    fun <T : Entity<out Serializable>> update(data: T)

    fun <T : Entity<out Serializable>> delete(query: QuerySupport, entityClass: Class<T>)

    fun <T : Entity<out Serializable>> deleteById(query: IDQuerySupport, entityClass: Class<T>)

    fun <T : Entity<out Serializable>> selectById(query: IDQuerySupport, entityClass: Class<T>): T?

    fun <T : Entity<out Serializable>> selectOne(query: QuerySupport,entityClass: Class<T>): T?

    fun <T : Entity<out Serializable>> selectByComplex(query: QuerySupport, entityClass: Class<T>): List<T>

    /**
     * 分页处理
     */
    fun <T : Entity<out Serializable>> selectByComplex(query: QuerySupport,pageable: Pageable,entityClass: Class<T>): Page<T>

    fun <T : Entity<out Serializable>> selectFirst(query: QuerySupport, entityClass: Class<T>): T

    fun <T : Entity<out Serializable>> selectFirstOrNull(query: QuerySupport, entityClass: Class<T>): T?

    /**
     * 根据条件统计
     */
    fun <T: Entity<out Serializable>> countBy(query: QuerySupport, entityClass: Class<T>): Long

}