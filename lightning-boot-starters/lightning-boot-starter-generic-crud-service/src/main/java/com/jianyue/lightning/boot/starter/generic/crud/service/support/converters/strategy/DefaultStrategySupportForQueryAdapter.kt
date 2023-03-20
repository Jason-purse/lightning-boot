package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy

import com.jianyue.lightning.boot.starter.generic.crud.service.entity.IdSupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.Converter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.JpaIdQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.mongo.MongoIdQuery

/**
 * @author FLJ
 * @date 2022/12/12
 * @time 15:59
 * @Description 能够转换为QuerySupport的 在校验组的情况下支持的实现 适配器
 *
 * 基于验证组的情况下,来实现 对应分类组的 参数转换为查询实现 !!!!
 */
interface DefaultValidationSupportForQueryAdapter<SOURCE : IdSupport<*>> :
    DefaultStrategySupportAdapter<SOURCE, QuerySupport>,
    Converter<SOURCE, QuerySupport> {


    override fun convert(param: SOURCE): QuerySupport? {
        return validationHandle(param);
    }
}

/**
 * 支持Mongo的forQuery adapter
 */
interface DefaultMongoValidationSupportForQueryAdapter<SOURCE : IdSupport<*>>:
    DefaultValidationSupportForQueryAdapter<SOURCE> {

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
interface DefaultJpaValidationSupportForQueryAdapter<SOURCE : IdSupport<*>>:
    DefaultValidationSupportForQueryAdapter<SOURCE> {
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