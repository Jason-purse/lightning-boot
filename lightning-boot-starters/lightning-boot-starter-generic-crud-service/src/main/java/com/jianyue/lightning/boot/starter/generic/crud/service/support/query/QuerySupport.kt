package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

import com.jianyue.lightning.framework.generic.crud.abstracted.param.AsSupport

/**
 * @date 2022/12/8
 * @time 22:42
 * @author FLJ
 * @since 2022/12/8
 * 查询支持标记接口, Query子类应该实现Query 接口,而不应该直接实现它 ...
 **/
interface QuerySupport :
    com.jianyue.lightning.framework.generic.crud.abstracted.param.AsSupport<QuerySupport> {

}




/**
 * id Query 标记接口
 */
interface IDQuerySupport : QuerySupport {

}

/**
 * pageQuery 标记接口
 */
interface PageQuerySupport : QuerySupport {

    /**
     *  可以做一些扩展,尝试
     */
    interface Pageable : org.springframework.data.domain.Pageable

    /**
     * 分页参数
     */
    fun getPage(): Pageable
}

/**
 * 不做任何查询
 */
interface NoneQuery: QuerySupport {
    companion object {
        private val query: NoneQuery = object: NoneQuery {

        }
        fun instance(): NoneQuery {
            return query;
        }
    }
}