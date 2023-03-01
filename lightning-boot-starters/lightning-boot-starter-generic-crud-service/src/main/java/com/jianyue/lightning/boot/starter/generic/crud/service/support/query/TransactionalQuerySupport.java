package com.jianyue.lightning.boot.starter.generic.crud.service.support.query;

import org.jetbrains.annotations.Nullable;

/**
 * 支持事务性操作的ParamSupport !!!!
 */
public interface TransactionalQuerySupport extends QuerySupport {

    /**
     * 获取一个事务定义,如果有,否则使用当前事务 或者空 !!!
     * 这里使用Object的原因是,不引入 事务相关的依赖 !!!
     * 但是实际上是一个真实的事务对象或者为空
     */
    @Nullable
    Object getTransactionDefinition();
}
