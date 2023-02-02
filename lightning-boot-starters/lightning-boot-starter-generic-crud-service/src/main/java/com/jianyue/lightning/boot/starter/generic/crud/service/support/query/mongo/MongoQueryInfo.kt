package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.mongo

import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.AbstractQueryInfo
import org.springframework.data.mongodb.core.query.Query
/**
 * @author FLJ
 * @date 2022/12/8
 * @time 17:18
 * @Description mongo query info
 */
public class MongoQueryInfo(nativeQuery: Query): AbstractQueryInfo<Query>(nativeQuery) {

}