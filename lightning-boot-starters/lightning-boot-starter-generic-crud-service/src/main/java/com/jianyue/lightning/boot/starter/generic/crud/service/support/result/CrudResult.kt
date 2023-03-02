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
     * 在java 环境中,不进行显式的类型强转 !!!
     */
    fun <T : Any?> getResult(): T

    /**
     * 判断是否有解决
     */
    fun hasResult(): Boolean

    fun hasError(): Boolean {
        return getCode() != 200
    }

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


        private val DATA_ERROR = AbstractCrudResult(null, 400, "No data was found that was needed.");

        private val DATA_CONFLICT = AbstractCrudResult(null, 400, "data conflicts.")

        fun success(): CrudResult {
            return EMPTY_SUCCESS;
        }

        // 有可能就是为空
        fun success(result: Any?): CrudResult {
            return AbstractCrudResult(result, 200, "success");
        }

        fun success(message: String): CrudResult {
            return AbstractCrudResult(null, 200, message);
        }

        /**
         * 自定义错误 !!!
         */
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

        fun dataConflict(): CrudResult {
            return DATA_CONFLICT;
        }



    }
}