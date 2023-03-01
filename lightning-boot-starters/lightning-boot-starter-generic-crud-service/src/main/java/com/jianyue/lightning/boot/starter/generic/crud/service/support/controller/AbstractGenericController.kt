package com.jianyue.lightning.boot.starter.generic.crud.service.support.controller

import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.*
import com.jianyue.lightning.boot.starter.generic.crud.service.support.result.CrudResult
import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.CrudService
import com.jianyue.lightning.boot.starter.util.dataflow.impl.InputContext
import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param


import org.springframework.web.bind.annotation.*

/**
 * @author FLJ
 * @date 2022/12/13
 * @time 10:14
 * @Description 通用模板方法
 *
 * 方法 open的原因是如果是cglib 代理,默认如果不可继承,则无法正确获取数据 !!!!
 *
 * 此通用crud 服务并不会影响事务的正常执行 !!!
 *
 * 因为可以基于 aop 实现声明式事务管理 !!!!
 */
abstract class AbstractGenericController<PARAM : Param,Service: CrudService<PARAM>>(private val service: Service) {

    // 为了参数解析(先初始化model)
    @ModelAttribute
    fun binder() {
        val old  = ControllerSupport.paramClassState.get()
        ControllerSupport.paramClassState.set(Tuple(getService().getParamClass(),old))
    }


    @GetMapping("list")
    @SelectListGroup
    open fun selectOperations(param: PARAM): CrudResult {
        return getService().selectOperation(InputContext.of(param))
    }


    @GetMapping
    @SelectByIdGroup
    open fun selectOperationById(param: PARAM): CrudResult {
        return getService().selectOperationById(InputContext.of(param))
    }

    @PostMapping
    @AddGroup
    open fun addOperation(@RequestBody param: Param): CrudResult {
        @Suppress("UNCHECKED_CAST")
        return getService().addOperation(InputContext.of(param as PARAM))
    }

    @PutMapping
    @UpdateGroup
    open fun updateOperation(@RequestBody param: Param): CrudResult {
        @Suppress("UNCHECKED_CAST")
        return getService().saveOperation(InputContext.of(param as PARAM))
    }

    @DeleteMapping
    @DeleteByIdGroup
    open fun deleteOperationById(param: PARAM): CrudResult {
        return getService().deleteOperationById(InputContext.of(param))
    }


    @DeleteMapping("criteria")
    @DeleteGroup
    open fun deleteOperation(param: PARAM): CrudResult {
        return getService().deleteOperation(InputContext.of(param))
    }

    /**
     * 子类继承服务
     */
    open fun getService(): Service {
        return service;
    }
}