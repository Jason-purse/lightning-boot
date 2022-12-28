package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
/**
 * @author FLJ
 * @date 2022/12/28
 * @time 14:34
 * @Description 基于entity 实现通用查询
 */
class DefaultJpaQuery(private val jpaQueryInfo: JpaQueryInfo<Entity>) : JpaQuery<Entity> {
    override fun getQueryInfo(): JpaQueryInfo<Entity> {
        return jpaQueryInfo;
    }
}