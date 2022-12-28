package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa

import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.AbstractIdQuery

/**
 * @author FLJ
 * @date 2022/12/28
 * @time 14:12
 * @Description 直接给出id ...
 */
class JpaIdQuery<ID : Any>(id: ID) : AbstractIdQuery<ID, JpaQueryInfo<ID>>(id, id.javaClass) {
    override fun getQueryInfo(): JpaQueryInfo<ID> {
        return JpaQueryInfo(getId())
    }
}
