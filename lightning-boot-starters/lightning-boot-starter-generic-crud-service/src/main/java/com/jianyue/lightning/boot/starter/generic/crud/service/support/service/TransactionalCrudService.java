package com.jianyue.lightning.boot.starter.generic.crud.service.support.service;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.TransactionalQuerySupport;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.result.CrudResult;
import com.jianyue.lightning.boot.starter.util.ThreadLocalSupport;
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

/**
 * 支持通用service 中进行事务调用 !!!
 * 子类实现,对于默认方法已经基于事务性调用,那么子类的自定义的实现的方法调用基于提供的方法
 * 去遵守事务性调用{@link  #getTransactionManager()}
 *
 * 此crudService 遵循事务定义 - 通过{@link TransactionalQuerySupport}
 */
public abstract class TransactionalCrudService<P extends Param, T extends Entity> extends AbstractCrudService<P, T> {

    /**
     * 来支持线程处理过程中保存的事务定义 !!
     */
    private final ThreadLocalSupport<TransactionDefinition> threadLocalSupport = ThreadLocalSupport.Companion.of();

    @NotNull
    private final PlatformTransactionManager transactionManager;

    @NotNull
    private final TransactionTemplate defaultTransactionTemplate;


    public TransactionalCrudService(@NotNull PlatformTransactionManager transactionManager,
                                    @NotNull
                                    TransactionDefinition transactionDefinition) {
        this.transactionManager = transactionManager;
        this.defaultTransactionTemplate = new TransactionTemplate(transactionManager,transactionDefinition);
    }

    public TransactionalCrudService(@NotNull PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        // 使用默认的事务定义 !!!
        this.defaultTransactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * 这里可以处理 QuerySupport 提供的事务信息处理 !!!
     */
    @NotNull
    @Override
    protected final CrudResult executeByQuerySupport(@Nullable QuerySupport querySupport, @NotNull Function1<? super QuerySupport, ? extends CrudResult> action) {
        if(querySupport instanceof TransactionalQuerySupport<?> transactionalQuerySupport) {
            Object transactionDefinition = transactionalQuerySupport.getTransactionDefinition();
            if(transactionDefinition != null) {
                TransactionTemplate transactionTemplate = new TransactionTemplate(getTransactionManager());
                return Objects.requireNonNull(
                        transactionTemplate.execute(new TransactionCallback<CrudResult>() {
                            @Override
                            public CrudResult doInTransaction(@NotNull TransactionStatus status) {
                                return TransactionalCrudService.super.executeByQuerySupport(querySupport, action);
                            }
                        }));
            }
        }

        assert querySupport != null;
        return super.executeByQuerySupport(querySupport,action);
    }




    @NotNull
    protected final PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }
}
