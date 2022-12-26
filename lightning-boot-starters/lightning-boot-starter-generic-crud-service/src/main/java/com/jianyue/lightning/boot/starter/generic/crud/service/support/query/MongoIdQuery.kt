package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * @date 2022/12/8
 * @time 21:16
 * @author FLJ
 * @since 2022/12/8
 *
 *
 * 默认的Mongo  IdQuery 实现
 **/
class MongoIdQuery<T: Any>(id: T) : AbstractIdQuery<T, MongoQueryInfo>(id, id.javaClass), MongoQuery {

    override fun getQueryInfo(): MongoQueryInfo {
        return MongoQueryInfo(Query.query(Criteria.where("id").`is`(getId())))
    }
}