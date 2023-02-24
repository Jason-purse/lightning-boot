package com.jianyue.lightning.boot.starter.generic.crud.service.support.result

import com.safone.order.service.model.order.verification.support.result.AbstractCrudResult

/**
 * @date 2022/12/8
 * @time 22:55
 * @author FLJ
 * @since 2022/12/8
 * CRUD 结果提示
 **/
interface CrudResult {
    /**
     * 如果有结果
     */
    fun getResult(): Any?

    /**
     * 判断是否有解决
     */
    fun hasResult(): Boolean


    /**
     * 如果说result 是一个 Collection
     */
    fun hasResults(): Boolean

    /**
     * 消息码
     */
    fun getCode(): Int

    /**
     * 消息
     */
    fun getMessage(): String


    companion object {

        inline fun <reified T> CrudResult.getResultFor(): T {
            return getResult() as T
        }



        /**
         * 空的成功
         */
        private val EMPTY_SUCCESS = AbstractCrudResult(null, 200, "success")

        /**
         * 服务器错误
         */
        private val SERVER_ERROR = AbstractCrudResult(null, 500, "server_internal_error");

        /**
         * 请求导致服务器内部计算的相关的错误
         */
        private val REQUEST_ERROR = AbstractCrudResult(null, 400, "operation error");


        private val DATA_ERROR = AbstractCrudResult(null,400,"no needed data");

        fun success(): CrudResult {
            return EMPTY_SUCCESS;
        }

        // 有可能就是为空
        fun success(result: Any?): CrudResult {
            return AbstractCrudResult(result, 200, "success");
        }

        fun success(message: String): CrudResult {
            return AbstractCrudResult(null,200,message);
        }


        fun error(code: Int, message: String): CrudResult {
            return AbstractCrudResult(null, code, message);
        }


        fun serverError(): CrudResult {
            return SERVER_ERROR;
        }


        fun error(message: String): CrudResult {
            return AbstractCrudResult(null, 400, message);
        }

        fun error(): CrudResult {
            return REQUEST_ERROR;
        }

        fun noData(): CrudResult {
            return DATA_ERROR;
        }

    }
}