package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.AbstractQuery

interface JpaEntityQuery<T : Entity> : AbstractQuery<JpaQueryInfo<T>>,JpaQuery<T> {
}