package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

import org.springframework.transaction.TransactionDefinition
/**
 * @author FLJ
 * @date 2023/3/2
 * @time 10:53
 * @Description 支持事务的查询定义支持 !!!
 */
interface TransactionalQuerySupport<T>: Query<T> {
    /**
     * 获取一个事务定义,如果有,否则使用当前事务 或者空 !!!
     */
    fun getTransactionDefinition(): TransactionDefinition?
}