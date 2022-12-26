package com.jianyue.lightning.boot.starter.generic.crud.service.support

import com.jianyue.lightning.boot.starter.generic.crud.service.support.EmptyQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.MongoQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.MongoQueryInfo
import org.springframework.data.mongodb.core.query.Query
/**
 * @author FLJ
 * @date 2022/12/9
 * @time 21:05
 * @Description mongo 空Query
 */
class MongoEmptyQuery: EmptyQuery<MongoQueryInfo>, MongoQuery {

     override fun getQueryInfo(): MongoQueryInfo {
        return MongoQueryInfo(Query())
    }
}

