package com.jianyue.lightning.boot.starter.generic.crud.service.support

import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.MongoQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.MongoQueryInfo

class DefaultMongoQuery(private val queryInfo: MongoQueryInfo): MongoQuery {
    override fun getQueryInfo(): MongoQueryInfo {
       return queryInfo;
    }
}