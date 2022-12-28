package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.AbstractQuery
/**
 * @author FLJ
 * @date 2022/12/28
 * @time 13:47
 * @Description jpa query
 */
interface JpaQuery<T: Entity>: AbstractQuery<JpaQueryInfo<T>> {
}