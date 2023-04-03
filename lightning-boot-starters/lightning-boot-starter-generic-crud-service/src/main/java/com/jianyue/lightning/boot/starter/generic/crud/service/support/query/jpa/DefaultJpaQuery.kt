package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import java.io.Serializable

/**
 * @author FLJ
 * @date 2022/12/28
 * @time 14:34
 * @Description 基于entity 实现通用查询
 */
class DefaultJpaQuery<E: Entity<out Serializable>>(private val jpaQueryInfo: JpaQueryInfo<E>) : JpaEntityQuery<E> {
    override fun getQueryInfo(): JpaQueryInfo<E> {
        return jpaQueryInfo;
    }
}