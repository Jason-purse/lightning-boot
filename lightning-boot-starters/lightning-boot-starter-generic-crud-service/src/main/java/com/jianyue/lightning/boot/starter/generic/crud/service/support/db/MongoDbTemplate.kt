package com.jianyue.lightning.boot.starter.generic.crud.service.support.db

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.Query
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.mongo.MongoQuery
import com.jianyue.lightning.boot.starter.util.BeanUtils
import com.jianyue.lightning.boot.starter.util.isNotNull
import com.jianyue.lightning.framework.generic.crud.abstracted.param.asNativeObject
import com.mongodb.BasicDBObject
import org.bson.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Update
import org.springframework.util.Assert
import java.io.Serializable


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

    override fun <T : Entity<out Serializable>> add(data: T) {
        mongoTemplate.save(data)
    }

    override fun <T : Entity<out Serializable>> addList(data: List<T>) {
        mongoTemplate.insertAll(data)
    }

    override fun <T : Entity<out Serializable>> saveList(data: List<T>) {
        val entities: MutableList<T> = ArrayList()
        val updateEntities: MutableList<T> = ArrayList()
        data.forEach {
            if (it.id.isNotNull()) {
                updateEntities.add(it)
            } else {
                entities.add(it)
            }
        }

        val bulkOperations: BulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, data[0].javaClass)

        if (updateEntities.isNotEmpty()) {
            updateEntities.associateBy { it.id }.apply {
                mongoTemplate.find(org.springframework.data.mongodb.core.query.Query(
                        Criteria.where("_id").`in`(updateEntities.map { it.id })
                ), data[0].javaClass)
                        .forEach {
                            val query = org.springframework.data.mongodb.core.query.Query(Criteria.where("_id").`is`(it.id))
                            val update = Document()
                            mongoTemplate.converter.write(BeanUtils.updateProperties(get(it.id)!!,it), update)
                            bulkOperations.updateOne(query, Update.fromDocument(update))
                        }
            }
        }

        if (entities.isNotEmpty()) {
            entities.forEach {
                bulkOperations.insert(it)
            }
        }

        bulkOperations.execute()
    }

    override fun <T : Entity<out Serializable>> update(data: T) {
        mongoTemplate.save(data)
    }


    override fun <T : Entity<out Serializable>> delete(query: QuerySupport, entityClass: Class<T>) {
        query.asNativeObject<MongoQuery>().also {
            mongoTemplate.remove(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }


    override fun <T : Entity<out Serializable>> selectByComplex(query: QuerySupport, entityClass: Class<T>): List<T> {
        query.asNativeObject<MongoQuery>().let {
            return mongoTemplate.find(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T : Entity<out Serializable>> deleteById(query: IDQuerySupport, entityClass: Class<T>) {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.remove(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T : Entity<out Serializable>> selectById(query: IDQuerySupport, entityClass: Class<T>): T? {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T : Entity<out Serializable>> selectOne(query: QuerySupport, entityClass: Class<T>): T? {
        val list = this.selectByComplex(query, entityClass)
        Assert.isTrue(list.isNotEmpty(), "need only one ,but return many result !!!")
        return list[0];
    }

    override fun <T : Entity<out Serializable>> selectFirst(query: QuerySupport, entityClass: Class<T>): T {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
                    ?: throw IllegalArgumentException("can't find one by query !!!")
        }
    }

    override fun <T : Entity<out Serializable>> selectFirstOrNull(query: QuerySupport, entityClass: Class<T>): T? {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.findOne(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }

    override fun <T : Entity<out Serializable>> selectByComplex(query: QuerySupport, pageable: Pageable, entityClass: Class<T>): Page<T> {
        return query.asNativeObject<MongoQuery>().let {
            val queryWithPage = it.getQueryInfo().getNativeQuery().with(pageable)
            PageImpl(mongoTemplate.find(queryWithPage, entityClass), pageable, mongoTemplate.count(queryWithPage, entityClass))
        }
    }

    override fun <T : Entity<out Serializable>> countBy(query: QuerySupport, entityClass: Class<T>): Long {
        return query.asNativeObject<MongoQuery>().let {
            mongoTemplate.count(it.getQueryInfo().getNativeQuery(), entityClass)
        }
    }
}