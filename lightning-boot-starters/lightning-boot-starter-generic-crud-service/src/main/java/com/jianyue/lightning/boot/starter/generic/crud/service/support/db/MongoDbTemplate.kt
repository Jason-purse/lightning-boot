package com.jianyue.lightning.boot.starter.generic.crud.service.support.db

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.mongo.MongoQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.framework.generic.crud.abstracted.param.asNativeObject
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.util.Assert

/**
 * @date 2022/12/8
 * @time 22:48
 * @author FLJ
 * @since 2022/12/8
 *
 * 采用低优先级就是为了能够在MongoTemplate 注入之后,判断存在bean
 **/
@ConditionalOnBean(MongoTemplate::class)
class MongoDbTemplate(private val mongoTemplate: MongoTemplate) : DBTemplate {

    override fun <T : Entity> add(data: T) {
        mongoTemplate.save(data)
    }

    override fun <T : Entity> addList(data: List<T>) {
        mongoTemplate.insertAll(data)
    }

    override fun <T : Entity> update(data: T) {
        mongoTemplate.save(data)
    }


    override fun <T : Entity> delete(query: QuerySupport, entityClass: Class<T>) {
        query.asNativeObject<MongoQuery>().also {
            mongoTemplate.remove(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }


    override fun <T : Entity> selectByComplex(query: QuerySupport, entityClass: Class<T>): List<T> {
        query.asNativeObject<MongoQuery>().let {
            return mongoTemplate.find(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T : Entity> deleteById(query: IDQuerySupport, entityClass: Class<T>) {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.remove(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T : Entity> selectById(query: IDQuerySupport, entityClass: Class<T>): T? {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T : Entity> selectOne(query: QuerySupport, entityClass: Class<T>): T? {
        val list = this.selectByComplex(query, entityClass)
        Assert.isTrue(list.isNotEmpty(), "need only one ,but return many result !!!")
        return list[0];
    }

    override fun <T : Entity> selectFirst(query: QuerySupport, entityClass: Class<T>): T {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
                ?: throw IllegalArgumentException("can't find one by query !!!")
        }
    }

    override fun <T : Entity> selectFirstOrNull(query: QuerySupport, entityClass: Class<T>): T? {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T : Entity> selectByComplex(query: QuerySupport, pageable: Pageable, entityClass: Class<T>): Page<T> {
        return query.asNativeObject<MongoQuery>().let {
            val queryWithPage= it.getQueryInfo().getNativeQuery().with(pageable)
            PageImpl(mongoTemplate.find(queryWithPage,entityClass),pageable,mongoTemplate.count(queryWithPage,entityClass))
        }
    }

    override fun <T : Entity> countBy(query: QuerySupport, entityClass: Class<T>): Long {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.count(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }
}