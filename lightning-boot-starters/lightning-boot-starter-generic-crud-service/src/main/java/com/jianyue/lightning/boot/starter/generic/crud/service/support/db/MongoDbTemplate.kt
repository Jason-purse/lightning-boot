package com.jianyue.lightning.boot.starter.generic.crud.service.support.db

import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.MongoQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.framework.generic.crud.abstracted.param.asNativeObject
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.mongodb.core.MongoTemplate

/**
 * @date 2022/12/8
 * @time 22:48
 * @author FLJ
 * @since 2022/12/8
 *
 * 采用低优先级就是为了能够在MongoTemplate 注入之后,判断存在bean
 **/
@AutoConfigureOrder(Int.MAX_VALUE)
@ConditionalOnBean(MongoTemplate::class)
class MongoDbTemplate(private val mongoTemplate: MongoTemplate) : DBTemplate {

    override fun <T : Any> add(data: T) {
        mongoTemplate.save(data)
    }

    override fun <T : Any> addList(data: List<T>) {
        mongoTemplate.insertAll(data)
    }

    override fun <T : Any> update(data: T) {
        mongoTemplate.save(data)
    }


    override fun <T> delete(query: QuerySupport, entityClass: Class<T>) {
        query.asNativeObject<MongoQuery>().also {
            mongoTemplate.remove(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }


    override fun <T> selectByComplex(query: QuerySupport, entityClass: Class<T>): List<T> {
        query.asNativeObject<MongoQuery>().let {
            return mongoTemplate.find(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T> deleteById(query: IDQuerySupport, entityClass: Class<T>) {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.remove(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T> selectById(query: IDQuerySupport, entityClass: Class<T>): T? {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T> selectFirst(query: QuerySupport, entityClass: Class<T>): T {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
                ?: throw IllegalArgumentException("can't find one by query !!!")
        }
    }

    override fun <T> selectFirstOrNull(query: QuerySupport, entityClass: Class<T>): T? {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T> countBy(query: QuerySupport, entityClass: Class<T>): Long {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.count(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }
}