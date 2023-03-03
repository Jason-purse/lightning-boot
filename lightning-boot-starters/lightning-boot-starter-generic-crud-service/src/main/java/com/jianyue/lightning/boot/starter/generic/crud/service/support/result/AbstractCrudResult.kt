package com.safone.order.service.model.order.verification.support.result

import com.fasterxml.jackson.annotation.JsonInclude
import com.jianyue.lightning.boot.starter.generic.crud.service.support.result.CrudResult

/**
 * @date 2022/12/8
 * @time 22:58
 * @author FLJ
 * @since 2022/12/8
 *
 *
 * 抽象的crud 结果处理
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
open class AbstractCrudResult(private val result: Any?, private val code: Int, private val message: String) :
    CrudResult {

    override fun <T> getResult(): T {
        return (T)this.result;
    }

    override fun hasResult(): Boolean {
        return result != null;
    }

    override fun hasResults(): Boolean {
        return hasResult() && result is Collection<*> && result.size > 0
    }

    override fun getCode(): Int {
        return code;
    }

    override fun getMessage(): String {
        return message;
    }

}