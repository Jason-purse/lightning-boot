package com.jianyue.lightning.boot.starter.generic.crud.service.support.service;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.result.CrudResult;
import com.jianyue.lightning.boot.starter.util.dataflow.impl.InputContext;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.TransactionalQuerySupport;
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
import kotlin.ExtensionFunctionType;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 支持通用service 中进行事务调用 !!!
 * 子类实现,对于默认方法已经基于事务性调用,那么子类的自定义的实现的方法调用基于提供的方法
 * 去遵守事务性调用{@link  #getTransactionManager()}
 *
 * 此crudService 遵循事务定义 - 通过{@link TransactionalQuerySupport}
 */
public abstract class TransactionalCrudService<P extends Param, T extends Entity> extends AbstractCrudService<P, T> {

    @NotNull
    private final PlatformTransactionManager transactionManager;
    public TransactionalCrudService(@NotNull PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @NotNull
    @Override
    public CrudResult executeByQueryConverter(@Nullable QuerySupport querySupport, @NotNull Function1<? super QuerySupport, ? extends CrudResult> action) {
        if(querySupport instanceof TransactionalQuerySupport) {
            Object transactionDefinition = ((TransactionalQuerySupport) querySupport).getTransactionDefinition();
            if(transactionDefinition instanceof TransactionDefinition) {
                TransactionTemplate transactionTemplate = new TransactionTemplate(getTransactionManager());
                return Objects.requireNonNull(
                        transactionTemplate.execute(new TransactionCallback<CrudResult>() {
                    @Override
                    public CrudResult doInTransaction(@NotNull TransactionStatus status) {
                        return TransactionalCrudService.super.executeByQueryConverter(querySupport, action);
                    }
                }));
            }
        }

        return super.executeByQueryConverter(querySupport,action);
    }

    @NotNull
    protected final PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }
}
