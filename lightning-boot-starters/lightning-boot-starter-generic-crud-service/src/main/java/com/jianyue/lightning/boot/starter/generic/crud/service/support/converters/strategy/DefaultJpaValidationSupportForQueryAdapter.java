package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy;

import com.jianyue.lightning.boot.starter.generic.crud.service.entity.IdSupport;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.JpaIdQuery;

public interface DefaultJpaValidationSupportForQueryAdapter <SOURCE extends IdSupport<?>> extends
        DefaultStrategySupportForQueryAdapter<SOURCE>{

    @Override
    default QuerySupport selectByIdGroupHandle(SOURCE source) {
        return new JpaIdQuery<>(source.getId());
    }

    @Override
    default QuerySupport updateGroupHandle(SOURCE source) {
        return new JpaIdQuery<>(source.getId());
    }

    @Override
    default QuerySupport deleteByIdGroupHandle(SOURCE source) {
        return new JpaIdQuery<>(source.getId());
    }

}
