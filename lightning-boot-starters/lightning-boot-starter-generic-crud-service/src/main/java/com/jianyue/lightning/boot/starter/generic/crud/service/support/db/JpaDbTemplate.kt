package com.jianyue.lightning.boot.starter.generic.crud.service.support.db

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.JpaIdQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.JpaQuery
import com.jianyue.lightning.framework.generic.crud.abstracted.param.asNativeObject
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.domain.Example
import org.springframework.data.jpa.repository.JpaContext
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import java.util.concurrent.ConcurrentHashMap


/**
 * @author FLJ
 * @date 2022/12/28
 * @time 10:58
 * @Description jdbc Template wrapper
 */
@ConditionalOnBean(value = [JpaContext::class])
open class JpaDbTemplate(private val context: JpaContext) : DBTemplate {

    private val emCache = ConcurrentHashMap<Class<*>, JpaRepositoryImplementation<*, *>>()

    private val idCache = ConcurrentHashMap<Class<*>, Class<*>>()

    @Transactional
    override fun <T : Entity> add(data: T) {
        getRepository(data.javaClass).save(data)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Entity> getRepository(clazz: Class<T>): JpaRepositoryImplementation<T, Any> {
        return emCache.computeIfAbsent(clazz) {
            val entityManager = context.getEntityManagerByManagedType(clazz)
            val entityInformation = JpaEntityInformationSupport.getEntityInformation(clazz, entityManager)

            // id 类型映射
            idCache[clazz] = entityInformation.idType;
            // id 类型不重要
            val repository: SimpleJpaRepository<T, Any> =
                SimpleJpaRepository<T, Any>(entityInformation, entityManager)
            repository
        } as JpaRepositoryImplementation<T, Any>
    }

    @Transactional
    override fun <T : Entity> addList(data: List<T>) {
        getRepository(data[0].javaClass).saveAll(data)
    }

    @Transactional
    override fun <T : Entity> update(data: T) {
        getRepository(data.javaClass).save(data)
    }

    @Transactional
    override fun <T : Entity> delete(query: QuerySupport, entityClass: Class<T>) {
        getRepository(entityClass).delete(query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery())
    }

    @Transactional
    override fun <T : Entity> deleteById(query: IDQuerySupport, entityClass: Class<T>) {
        val idQuery = query.asNativeObject<JpaIdQuery<*>>()
        val idType = idCache[entityClass]
        // 强制判断 ..
        Assert.isTrue(idType == idQuery.getIdClass(), "id type must be equals !!!")
        getRepository(entityClass).deleteById(idQuery.getQueryInfo())
    }

    override fun <T : Entity> selectById(query: IDQuerySupport, entityClass: Class<T>): T? {
        val idQuery = query.asNativeObject<JpaIdQuery<*>>()
        // 强制判断 ..
        return getRepository(entityClass).let {
            val idType = idCache[entityClass]
            Assert.isTrue(idType == idQuery.getIdClass(), "id type must be equals !!!")
            it.findById(idQuery.getQueryInfo().getNativeQuery()).orElse(null)
        }
    }

    override fun <T : Entity> selectByComplex(query: QuerySupport, entityClass: Class<T>): List<T> {
        return getRepository(entityClass).findAll(
            Example.of(
                query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
            )
        )
    }

    override fun <T : Entity> selectFirst(query: QuerySupport, entityClass: Class<T>): T {
        return getRepository(entityClass).findOne(
            Example.of(
                query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
            )
        ).orElseThrow {
            IllegalArgumentException("can't find first by query criteria: $query")
        }
    }

    override fun <T : Entity> selectFirstOrNull(query: QuerySupport, entityClass: Class<T>): T? {
        return getRepository(entityClass).findOne(
            Example.of(
                query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
            )
        ).orElse(null)
    }

    override fun <T : Entity> countBy(query: QuerySupport, entityClass: Class<T>): Long {
        return getRepository(entityClass).count(
            Example.of(
                query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
            )
        )
    }
}