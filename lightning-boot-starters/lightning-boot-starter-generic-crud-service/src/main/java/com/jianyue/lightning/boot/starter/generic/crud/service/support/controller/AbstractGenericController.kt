package com.jianyue.lightning.boot.starter.generic.crud.service.support.controller

import com.jianyue.lightning.boot.starter.generic.crud.service.support.result.CrudResult
import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.CrudService
import com.jianyue.lightning.boot.starter.generic.crud.service.support.validates.*
import com.jianyue.lightning.boot.starter.util.dataflow.impl.InputContext
import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param


import org.springframework.web.bind.annotation.*

/**
 * @author FLJ
 * @date 2022/12/13
 * @time 10:14
 * @Description 通用模板方法
 */
abstract class AbstractGenericController<PARAM : Param,Service: CrudService<PARAM>>(private val service: Service) {

    // 为了参数解析(先初始化model)
    @ModelAttribute
    @ValidationAnnotation
    fun binder() {
        val old  = ControllerSupport.paramClassState.get()
        ControllerSupport.paramClassState.set(Tuple(getService().getParamClass(),old))
    }


    @GetMapping("list")
    @ValidationAnnotation
    open fun selectOperations(@SelectListGroup param: PARAM): CrudResult {
        return getService().selectOperation(InputContext.of(param))
    }


    @GetMapping
    @ValidationAnnotation
    open fun selectOperationById(@SelectByIdGroup param: PARAM): CrudResult {
        return getService().selectOperationById(InputContext.of(param))
    }

    @PostMapping
    @ValidationAnnotation
    open fun addOperation(@AddGroup @RequestBody param: Param): CrudResult {
        @Suppress("UNCHECKED_CAST")
        return getService().addOperation(InputContext.of(param as PARAM))
    }

    @PutMapping
    @ValidationAnnotation
    open fun updateOperation(@UpdateGroup @RequestBody param: Param): CrudResult {
        @Suppress("UNCHECKED_CAST")
        return getService().saveOperation(InputContext.of(param as PARAM))
    }

    @DeleteMapping
    @ValidationAnnotation
    open fun deleteOperationById(@DeleteByIdGroup param: PARAM): CrudResult {
        return getService().deleteOperationById(InputContext.of(param))
    }


    @ValidationAnnotation
    @DeleteMapping("criteria")
    open fun deleteOperation(@DeleteGroup param: PARAM): CrudResult {
        return getService().deleteOperation(InputContext.of(param))
    }

    /**
     * 子类继承服务
     */
    protected open fun getService(): Service {
        return service;
    }
}