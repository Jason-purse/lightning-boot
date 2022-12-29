package com.jianyue.lightning.boot.starter.generic.crud.service.support.service


import com.jianyue.lightning.boot.starter.generic.crud.service.support.AbstractConverterAdapter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.BasedParamFreeEntityConverter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.DefaultGenericConverterAdapter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.EntityConverter
import com.safone.order.service.model.order.verification.support.converters.QueryConverter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.DBTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.NoneQuery
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.result.CrudResult
import com.jianyue.lightning.boot.starter.util.BeanUtils
import com.jianyue.lightning.boot.starter.util.dataflow.impl.InputContext
import com.jianyue.lightning.boot.starter.util.isNotNull
import com.jianyue.lightning.boot.starter.util.isNull
import com.jianyue.lightning.framework.generic.crud.abstracted.param.asNativeObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.ResolvableType
import java.lang.reflect.ParameterizedType
import kotlin.math.log

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 9:38
 * @Description 抽象CRUD
 *
 *
 * 实现注意: param 转换为 query 可以为 null / noneQuery / byQuery / otherQuery, 具体业务根据传递的param进入对应的 converter 转换出 query
 *
 * 这里的add / addOperations / save 没有强制要求需要query 来检查是否有对应的唯一性数据条件断言,这取决于业务 ..(其实最终业务就是各种converter,来决定转换出符合条件的query / entity) ..
 *
 * 其他的select / delete / *ById( 对null / noneQuery /IdQuery 进行了严格的控制),这与上面的三个操作相反,子类例如可以增强功能,例如传递null的时候,删除或者查询所有数据 ...
 */
abstract class AbstractCrudService<PARAM : Param, ENTITY : Entity> : CrudService<PARAM>,
    ApplicationContextAware,
    DisposableBean {


    @Autowired
    private lateinit var dbTemplate: DBTemplate


    // 只要符合java assignableFrom 以及 逻辑 assignableFrom 语义即可 ...
    private val queryConverters: AbstractConverterAdapter<PARAM, QuerySupport>

    private val entityConverters: AbstractConverterAdapter<PARAM, out Entity>

    private val queryForListConverters: AbstractConverterAdapter<List<PARAM>, QuerySupport>

    /**
     * 具体可用的entity 参数 class 表现(也可以是接口),因为最终的entity 转换交给支持子类的entity converter ..
     */
    private val entityClass: Class<ENTITY>

    /**
     * 具体可用的参数class 表现(也就是接口),因为最终的query转换交给 实际支持子类的queryConverter ..
     */
    private val paramClass: Class<PARAM>

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)


    init {


        // 解析目标类型
        ResolvableType.forType(this.javaClass)
            .`as`(AbstractCrudService::class.java)
            .let {
                if (it.getGeneric(0) == ResolvableType.NONE || it.getGeneric(1) == ResolvableType.NONE) {
                    throw IllegalArgumentException("param or entity class must need provide !!")
                }
                val param = it.getGeneric(0).resolve()
                val entity = it.getGeneric(1).resolve()
                if (param == null || entity == null) {
                    throw IllegalArgumentException("param or entity must be class !!")
                }

                @Suppress("UNCHECKED_CAST")
                this.paramClass = param as Class<PARAM>
                @Suppress("UNCHECKED_CAST")
                this.entityClass = entity as Class<ENTITY>
            }

        // because erase generic type, we need to resolve abstract parent class to source class or target class ...
        @Suppress("UNCHECKED_CAST")
        resolveReservedTypeInfo(this.javaClass).let {
            this.queryConverters = DefaultGenericConverterAdapter.of(it, QuerySupport::class.java)
            // it's ok,even if use uncheck_cast, but members of entityConverters is safe ...
            this.entityConverters = DefaultGenericConverterAdapter.of(it, getEntityClass())
            this.queryForListConverters = DefaultGenericConverterAdapter.of(
                ResolvableType.forClassWithGenerics(List::class.java, it).type,
                QuerySupport::class.java
            )
        }

    }


    // 自己增加Converters ..

    fun addQueryConverters(vararg queryConverter: QueryConverter<PARAM>) {
        queryConverters.addConverters(* queryConverter)
    }

    fun addEntityConverters(vararg converters: EntityConverter<PARAM, out ENTITY>) {
        entityConverters.noSafeAddConverters(* converters)
    }

    fun addQueryForListConverters(vararg queryConverter: QueryConverter<List<PARAM>>) {
        queryForListConverters.addConverters(* queryConverter)
    }


    /**
     * 解析保留的类型信息,但是需要注意,有可能还是拿不到信息,因为它可能是一个泛型类 ..
     */
    @Suppress("UNCHECKED_CAST")
    private fun resolveReservedTypeInfo(clazz: Class<AbstractCrudService<PARAM, ENTITY>>): Class<PARAM> {
        val resolvableType = ResolvableType.forType(clazz).`as`(AbstractCrudService::class.java)
        resolvableType.type.let {
            if (it is ParameterizedType) {
                return it.actualTypeArguments[0] as Class<PARAM>
            }
        }
        throw IllegalArgumentException("can't get actualTypeArgument for  TypeParameter 'PARAM' of AbstractCrudService,Please use a true generic for kotlin or provide a java class !!!")
    }




    override fun addOperation(context: InputContext<PARAM>): CrudResult {

        choiceQueryConverterAndInvoke(context)?.let {
            if (it !is NoneQuery) {
                val one = getDbTemplate().selectFirstOrNull(
                    it,
                    getEntityClass()
                )
                // 没有查到,可以正常新增
                // for user
                if (one.isNotNull()) {
                    return CrudResult.error()
                }
            }
        }

        getDbTemplate().add(choiceEntityConverterAndInvoke(context).asNativeObject<Entity>().apply {
            // 回调
            saveFill();
        })
        return CrudResult.success()
    }


    protected fun choiceQueryConverterAndInvoke(context: InputContext<PARAM>): QuerySupport? {
        if (queryConverters.support(context.dataFlow)) {
            queryConverters.convert(context.dataFlow).let {
                if (it.isNotNull()) {
                    return it!!
                }
            }
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    protected fun choiceEntityConverterAndInvoke(context: InputContext<PARAM>): ENTITY {
        if (entityConverters.support(context.dataFlow)) {
            entityConverters.convert(context.dataFlow).let {
                if (it.isNotNull()) {
                    return it!! as ENTITY
                }
            }
        }
        throw IllegalArgumentException("can't convert param to entity !!!")
    }

    protected fun choiceQueryForListConverterAndInvoke(context: InputContext<List<PARAM>>): QuerySupport? {
        if (queryForListConverters.support(context.dataFlow)) {
            queryForListConverters.convert(context.dataFlow).let {
                if (it.isNotNull()) {
                    return it!!
                }
            }
        }
        return null
    }


    override fun addOperations(context: InputContext<List<PARAM>>): CrudResult {

        choiceQueryForListConverterAndInvoke(context)?.let {
            if (it !is NoneQuery) {
                if (!getDbTemplate().countBy(
                        it,
                        getEntityClass()
                    ).isNull()
                ) {
                    // for user
                    return CrudResult.error()
                }
            }
        }

        // 批量,这里按道理来说,我们应该判断的,但是也可以省略掉 ..
        // 目前 mongo 我们并没有配置唯一性索引,所以如果出错,也可以交给数据库抛出异常,集体出错
        getDbTemplate().addList(choiceEntityForListConverterAndInvoke(context).onEach { it.saveFill() })
        return CrudResult.success()
    }


    protected fun choiceEntityForListConverterAndInvoke(context: InputContext<List<PARAM>>): List<ENTITY> {
        return context.dataFlow.map { choiceEntityConverterAndInvoke(InputContext.of(it)) }
    }

    override fun saveOperation(context: InputContext<PARAM>): CrudResult {
        choiceQueryConverterAndInvoke(context)?.let {
            if (it !is NoneQuery) {
                context.run {
                    val one: Entity? =
                        getDbTemplate().selectFirstOrNull(
                            it,
                            getEntityClass()
                        )
                    if (one.isNull()) {
                        return CrudResult.noData()
                    }

                    BeanUtils.updateProperties(choiceEntityConverterAndInvoke(context), one!!)

                    getDbTemplate().update(one.apply { updateFill() })
                    return CrudResult.success(one)
                }
            }
        }

        // for user
        return CrudResult.noData()
    }

    override fun selectOperation(context: InputContext<PARAM>): CrudResult {
        choiceQueryConverterAndInvoke(context).let {
            if (baseQueryCriteriaAssert(it)) {
                getDbTemplate().selectByComplex(it!!, getEntityClass()).run {
                    return CrudResult.success(this)
                }
            }
        }
        throw IllegalArgumentException("select must have one query for get data,and can't use noneQuery !!!")
    }

    override fun deleteOperation(context: InputContext<PARAM>): CrudResult {
        choiceQueryConverterAndInvoke(context).let {
            if (baseQueryCriteriaAssert(it)) {
                getDbTemplate().delete(it!!, getEntityClass())
            }
        }

        throw IllegalArgumentException("delete must have one query for delete data,and can't use noneQuery !!!")
    }

    protected fun baseQueryCriteriaAssert(it: QuerySupport?): Boolean {
        return it.isNotNull() && it !is NoneQuery
    }

    override fun selectOperationById(context: InputContext<PARAM>): CrudResult {

        choiceQueryConverterAndInvoke(context).let {
            if (byIdQueryCriteriaAssert(it)) {
                getDbTemplate().selectById(it!!.asNativeObject(), getEntityClass()).run {
                    if (isNull()) {
                        return CrudResult.noData()
                    } else {
                        return CrudResult.success(this)
                    }
                }
            }
        }

        throw IllegalArgumentException("selectById must have one query for get data,and must use IdQuery !!!")
    }

    override fun deleteOperationById(context: InputContext<PARAM>): CrudResult {
        choiceQueryConverterAndInvoke(context).let {
            if (byIdQueryCriteriaAssert(it)) {
                getDbTemplate().deleteById(it!!.asNativeObject(), getEntityClass())
                return CrudResult.success()
            }
        }

        throw IllegalArgumentException("deleteById must have one query for get data,and must use IdQuery !!!")
    }


    protected fun byIdQueryCriteriaAssert(it: QuerySupport?): Boolean {
        return it.isNotNull() && it !is NoneQuery && it is IDQuerySupport
    }

    override fun getDbTemplate(): DBTemplate {
        return dbTemplate
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {

        // 使用spring 的自动装配能力,但是这些bean spring 不负责生命周期调用,所以我们需要负责
        applicationContext.autowireCapableBeanFactory.autowireBean(queryConverters);
        applicationContext.autowireCapableBeanFactory.initializeBean(
            queryConverters,
            this.javaClass.simpleName + "queryConverters"
        )

        applicationContext.autowireCapableBeanFactory.autowireBean(entityConverters);
        applicationContext.autowireCapableBeanFactory.initializeBean(
            entityConverters,
            this.javaClass.simpleName + "entityConverters"
        );

        applicationContext.autowireCapableBeanFactory.autowireBean(queryForListConverters);
        applicationContext.autowireCapableBeanFactory.initializeBean(
            queryForListConverters,
            this.javaClass.simpleName + "queryForListConverters"
        );

        // 这是默认情况,这样,能够通过java bean 赋值的方式进行转换 ..
        if (entityConverters.getConverters().isEmpty()) {
            entityConverters.noSafeAddConverters(BasedParamFreeEntityConverter(paramClass, entityClass));
        }
        logger.info("${this.javaClass.name} added queryConverters is {}",queryConverters.getConverters())
        logger.info("${this.javaClass.name} added queryForListConverters is {}",queryForListConverters.getConverters())
        logger.info("${this.javaClass.name} added entityConverters is {}", entityConverters.getConverters());
    }

    override fun destroy() {
        queryConverters.destroy()
        entityConverters.destroy()
        queryForListConverters.destroy()
    }

    /**
     * 具体生成目标Entity的class
     */
    // 子类可以覆盖 ..
    override fun getEntityClass(): Class<out Entity> {
        return entityClass
    }

    // 子类可以覆盖 ..
    override fun getParamClass(): Class<out Param> {
        return paramClass
    }
}