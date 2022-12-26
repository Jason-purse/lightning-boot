package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

/**
 * @author FLJ
 * @date 2022/12/8
 * @time 17:11
 * @Description 查询标记记录
 */
interface Query<T> : QuerySupport {
    /**
     * 能够获取query 条件
     */
    fun getQueryInfo(): T
}