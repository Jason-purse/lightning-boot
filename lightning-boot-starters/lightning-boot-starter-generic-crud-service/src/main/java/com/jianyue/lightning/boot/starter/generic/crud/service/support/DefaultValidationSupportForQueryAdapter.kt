package com.jianyue.lightning.boot.starter.generic.crud.service.support

import com.jianyue.lightning.boot.starter.generic.crud.service.entity.IdSupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.Converter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.JpaIdQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.mongo.MongoIdQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.validates.DefaultValidationSupportAdapter

/**
 * @author FLJ
 * @date 2022/12/12
 * @time 15:59
 * @Description 能够转换为QuerySupport的 适配器
 */
interface DefaultValidationSupportForQueryAdapter<SOURCE : IdSupport<*>> :
    DefaultValidationSupportAdapter<SOURCE, QuerySupport>,
    Converter<SOURCE, QuerySupport> {


    override fun convert(param: SOURCE): QuerySupport? {
        return validationHandle(param);
    }
}

/**
 * 支持Mongo的forQuery adapter
 */
interface DefaultMongoValidationSupportForQueryAdapter<SOURCE : IdSupport<*>>: DefaultValidationSupportForQueryAdapter<SOURCE> {

    override fun selectByIdGroupHandle(s: SOURCE): QuerySupport? {
        return MongoIdQuery(s.id)
    }


    override fun deleteByIdGroupHandle(s: SOURCE): QuerySupport? {
        return MongoIdQuery(s.id)
    }

    override fun updateGroupHandle(s: SOURCE): QuerySupport {
        return MongoIdQuery(s.id)
    }
}

/**
 * 支持 jpa的 forQuery adapter
 */
interface DefaultJpaValidationSupportForQueryAdapter<SOURCE : IdSupport<*>>: DefaultValidationSupportForQueryAdapter<SOURCE> {
    override fun selectByIdGroupHandle(s: SOURCE): QuerySupport? {
        return JpaIdQuery(s.id)
    }


    override fun deleteByIdGroupHandle(s: SOURCE): QuerySupport? {
        return JpaIdQuery(s.id)
    }

    override fun updateGroupHandle(s: SOURCE): QuerySupport {
        return JpaIdQuery(s.id)
    }
}