package com.jianyue.lightning.boot.starter.generic.crud.service.support.db

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.ByIdSpecification
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.JpaIdQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.JpaQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa.JpaSpecificationQuery
import com.jianyue.lightning.boot.starter.util.isNotNull
import com.jianyue.lightning.framework.generic.crud.abstracted.param.asNativeObject
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaContext
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import java.io.Serializable
import java.util.*
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

    private val entityInfoCache = ConcurrentHashMap<Class<*>, JpaEntityInformation<*, *>>()

    private val idCache = ConcurrentHashMap<Class<*>, Class<*>>()

    @Transactional
    override fun <T : Entity<out Serializable>> add(data: T) {
        getRepository(data.javaClass).save(data)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Entity<out Serializable>> getRepository(clazz: Class<T>): JpaRepositoryImplementation<T, Any> {
        return emCache.computeIfAbsent(clazz) {
            val entityManager = context.getEntityManagerByManagedType(clazz)
            val entityInformation = JpaEntityInformationSupport.getEntityInformation(clazz, entityManager)

            // id 类型映射
            idCache[clazz] = entityInformation.idType;
            entityInfoCache[clazz] = entityInformation
            // id 类型不重要
            val repository: SimpleJpaRepository<T, Any> =
                SimpleJpaRepository<T, Any>(entityInformation, entityManager)
            repository
        } as JpaRepositoryImplementation<T, Any>
    }

    @Transactional
    override fun <T : Entity<out Serializable>> addList(data: List<T>) {
        getRepository(data[0].javaClass).saveAll(data)
    }

    override fun <T : Entity<out Serializable>> saveList(data: List<T>) {
        addList(data)
    }

    @Transactional
    override fun <T : Entity<out Serializable>> update(data: T) {
        getRepository(data.javaClass).save(data)
    }

    @Transactional
    override fun <T : Entity<out Serializable>> delete(query: QuerySupport, entityClass: Class<T>) {
        Assert.isTrue(query is JpaQuery<*>, "query must be jpaQuery type !!!");
        if (query is IDQuerySupport) {
            return deleteById(query, entityClass)
        }

        if(query is JpaSpecificationQuery<*>) {
            // 先查在删除 ...
            return selectByComplex(query, entityClass).let {
                if (it.isNotEmpty()) {
                    getRepository(entityClass).deleteAll(it)
                }
            }
        }

        getRepository(entityClass).delete(query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery())
    }

    @Transactional
    override fun <T : Entity<out Serializable>> deleteById(query: IDQuerySupport, entityClass: Class<T>) {
        Assert.isTrue(query is JpaQuery<*>, "query must be jpaQuery type !!!");
        val idQuery = query.asNativeObject<JpaIdQuery<*>>()
        getRepository(entityClass).let {
            val idType = idCache[entityClass]
            // 强制判断 ..
            Assert.isTrue(idType == idQuery.getIdClass(), "id type must be equals !!!")
            it.deleteById(idQuery.getQueryInfo().getNativeQuery())
        }
    }

    override fun <T : Entity<out Serializable>> selectById(query: IDQuerySupport, entityClass: Class<T>): T? {
        Assert.isTrue(query is JpaQuery<*>, "query must be jpaQuery type !!!");
        val idQuery = query.asNativeObject<JpaIdQuery<*>>()
        // 强制判断 ..
        return getRepository(entityClass).let {
            val idType = idCache[entityClass]
            Assert.isTrue(idType == idQuery.getIdClass(), "id type must be equals !!!")
            it.findById(idQuery.getQueryInfo().getNativeQuery()).orElse(null)
        }
    }

    override fun <T : Entity<out Serializable>> selectOne(query: QuerySupport, entityClass: Class<T>): T? {
        val list = this.selectByComplex(query, entityClass)
        Assert.isTrue(list.isNotEmpty(), "need only one,but return many result !!!")
        return list[0]
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Entity<out Serializable>> selectByComplex(query: QuerySupport, entityClass: Class<T>): List<T> {
        Assert.isTrue(query is JpaQuery<*>, "query must be jpaQuery type !!!");
        return when (query) {
            is IDQuerySupport -> selectById(query, entityClass).let {
                if (it.isNotNull()) {
                    listOf(it!!)
                } else {
                    Collections.emptyList()
                }
            }
            is JpaSpecificationQuery<*> -> getRepository(entityClass).findAll((query as JpaSpecificationQuery<T>).getQueryInfo().getNativeQuery())
            else -> getRepository(entityClass).findAll(
                Example.of(
                    query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
                )
            )
        }
    }

    // 分页处理 ..
    @Suppress("UNCHECKED_CAST")
    override fun <T : Entity<out Serializable>> selectByComplex(query: QuerySupport, pageable: Pageable, entityClass: Class<T>): Page<T> {
        Assert.isTrue(query is JpaQuery<*>, "query must be jpaQuery type !!!");
        return when (query) {
            is IDQuerySupport -> selectById(query, entityClass).let {
                if (it.isNotNull()) {
                    PageImpl(listOf(it!!),pageable,1)
                } else {
                    Page.empty()
                }
            }
            is JpaSpecificationQuery<*> -> getRepository(entityClass).findAll((query as JpaSpecificationQuery<T>).getQueryInfo().getNativeQuery(),
                    pageable)
            else -> getRepository(entityClass).findAll(
                    Example.of(
                            query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
                    ),
                    pageable
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Entity<out Serializable>> selectFirst(query: QuerySupport, entityClass: Class<T>): T {
        Assert.isTrue(query is JpaQuery<*>, "query must be jpaQuery type !!!");
        return when (query) {
            is IDQuerySupport -> Optional.ofNullable(selectById(query, entityClass))
            is JpaSpecificationQuery<*> -> getRepository(entityClass)
                    .findOne((query as JpaSpecificationQuery<T>).getQueryInfo().getNativeQuery())
            else -> getRepository(entityClass).findOne(
                Example.of(
                    query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
                )
            )
        }.orElseThrow {
            IllegalArgumentException("can't find first by query criteria: $query")
        }

    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Entity<out Serializable>> selectFirstOrNull(query: QuerySupport, entityClass: Class<T>): T? {
        Assert.isTrue(query is JpaQuery<*>, "query must be jpaQuery type !!!");
        return when (query) {
            is IDQuerySupport -> Optional.ofNullable(selectById(query, entityClass))
            is JpaSpecificationQuery<*> -> getRepository(entityClass)
                    .findOne((query as JpaSpecificationQuery<T>).getQueryInfo().getNativeQuery())
            else -> getRepository(entityClass).findOne(
                Example.of(
                    query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
                )
            )
        }.orElse(null)
    }

    override fun <T : Entity<out Serializable>> countBy(query: QuerySupport, entityClass: Class<T>): Long {
        Assert.isTrue(query is JpaQuery<*>, "query must be jpaQuery type !!!");
        if (query is IDQuerySupport) {
            @Suppress("UNCHECKED_CAST")
            return getRepository(entityClass).count(
                ByIdSpecification(
                    entityInfoCache[entityClass] as JpaEntityInformation<T, *>,
                    query.asNativeObject<JpaIdQuery<*>>().getQueryInfo().getNativeQuery()
                )
            )
        }

        if(query is JpaSpecificationQuery<*>) {
            getRepository(entityClass).count(query.asNativeObject<JpaSpecificationQuery<T>>().getQueryInfo().getNativeQuery())
        }

        return getRepository(entityClass).count(
            Example.of(
                query.asNativeObject<JpaQuery<T>>().getQueryInfo().getNativeQuery()
            )
        )
    }
}