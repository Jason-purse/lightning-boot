package com.jianyue.lightning.boot.starter.generic.crud.service.query

import com.jianyue.lightning.boot.starter.generic.crud.service.support.SPI
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.IDQuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import org.springframework.beans.BeanUtils
import org.springframework.lang.NonNull
import java.util.concurrent.ConcurrentHashMap

/**
 * @date 2022/12/8
 * @time 20:33
 * @author FLJ
 * @since 2022/12/8
 * 查询助手
 *
 * 对查询形成一种约定 ..
 **/
internal class QueryAssist {

    companion object {

        private val queryClassMap: MutableMap<Class<out QuerySupport>, Class<Any>> =
            ConcurrentHashMap<Class<out QuerySupport>, Class<Any>>()

        private fun getQueryClassMap(): MutableMap<Class<out QuerySupport>, Class<Any>> {
            return queryClassMap;
        }

        /**
         * 通过 id 进行Query 产生
         *
         * 这里通过 idQuerySupport 进行泛型 不想管处理
         *
         * 查看测试用例 QueryAssistTests
         */
        @NonNull
        inline fun <reified T : IDQuerySupport, reified ID> byIdAndQueryType(id: ID): IDQuerySupport {
            try {
                return BeanUtils.instantiateClass(
                    T::class.java.getConstructor(Any::class.java),
                    id
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("class can't instantiate !!!")
            }
        }



        fun <ID> byId(id: ID): IDQuerySupport {
            try {
                return getQuerySupportClass(IDQuerySupport::class.java).let {
                    BeanUtils.instantiateClass(
                        it.getConstructor(Any::class.java),
                        id
                    ) as IDQuerySupport
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("class can't instantiate !!!")
            }
        }

        fun getQuerySupportClass(clazz: Class<out QuerySupport>): Class<Any> {
            return getQueryClassMap().let {
                if (it.containsKey(clazz)) {
                    it[clazz]!!
                } else {
                    // 查询并发现缓存
                    val type: Class<Any> = SPI.findImplementationNoNeedType(clazz)
                    it[clazz] = type
                    type
                }
            }
        }
    }
}