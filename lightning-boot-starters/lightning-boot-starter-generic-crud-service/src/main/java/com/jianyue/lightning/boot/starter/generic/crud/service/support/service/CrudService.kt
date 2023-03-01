package com.jianyue.lightning.boot.starter.generic.crud.service.support.service

import com.jianyue.lightning.boot.starter.generic.crud.service.support.db.DBTemplate
import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import com.jianyue.lightning.boot.starter.generic.crud.service.support.result.CrudResult
import com.jianyue.lightning.boot.starter.util.dataflow.impl.InputContext

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 9:24
 * @Description 抽象出 Crud 基本操作
 *
 * 面向每一个Service 划分的业务,进行的统一抽象处理 ...
 *
 * 从这一次抽象中,学习到了,应该使用Type,而不是具体的类,例如Collection本身就是泛型,无法用类表示参数化类型 ...
 */
interface CrudService<PARAM: Param> {

    /**
     * 增加操作
     */
    fun addOperation(context: InputContext<PARAM>): CrudResult

    /**
     * 批量增加操作
     */
    fun addOperations(context: InputContext<List<PARAM>>): CrudResult

    /**
     * 保存操作
     */
    fun saveOperation(context: InputContext<PARAM>): CrudResult


    fun selectOperation(context: InputContext<PARAM>): CrudResult

    fun selectOperationById(context: InputContext<PARAM>): CrudResult


    fun deleteOperation(context: InputContext<PARAM>): CrudResult

    fun deleteOperationById(context: InputContext<PARAM>): CrudResult


    fun getEntityClass(): Class<out Entity>

    fun getParamClass(): Class<out Param>
}