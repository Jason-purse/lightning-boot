package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.mongo

import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.mongo.MongoQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.mongo.MongoQueryInfo

class DefaultMongoQuery(private val queryInfo: MongoQueryInfo): MongoQuery {
    override fun getQueryInfo(): MongoQueryInfo {
       return queryInfo;
    }
}