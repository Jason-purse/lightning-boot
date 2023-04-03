package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.AbstractQuery
import java.io.Serializable

interface JpaEntityQuery<T : Entity<out Serializable>> : AbstractQuery<JpaQueryInfo<T>>,JpaQuery<T> {
}