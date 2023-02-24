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
    @SelectListGroup
    open fun selectOperations(param: PARAM): CrudResult {
        ValidationSupport.setSelectListGroup()
        return getService().selectOperation(InputContext.of(param))
    }


    @GetMapping
    @ValidationAnnotation
    @SelectByIdGroup
    open fun selectOperationById(param: PARAM): CrudResult {
        ValidationSupport.setSelectByIdGroup()
        return getService().selectOperationById(InputContext.of(param))
    }

    @PostMapping
    @ValidationAnnotation
    @AddGroup
    open fun addOperation(@RequestBody param: Param): CrudResult {
        ValidationSupport.setAddGroup()
        @Suppress("UNCHECKED_CAST")
        return getService().addOperation(InputContext.of(param as PARAM))
    }

    @PutMapping
    @ValidationAnnotation
    @UpdateGroup
    open fun updateOperation(@RequestBody param: Param): CrudResult {
        ValidationSupport.setUpdateGroup()
        @Suppress("UNCHECKED_CAST")
        return getService().saveOperation(InputContext.of(param as PARAM))
    }

    @DeleteMapping
    @ValidationAnnotation
    @DeleteByIdGroup
    open fun deleteOperationById(param: PARAM): CrudResult {
        ValidationSupport.setDeleteByIdGroup()
        return getService().deleteOperationById(InputContext.of(param))
    }


    @ValidationAnnotation
    @DeleteMapping("criteria")
    @DeleteGroup
    open fun deleteOperation(param: PARAM): CrudResult {
        ValidationSupport.setDeleteGroup()
        return getService().deleteOperation(InputContext.of(param))
    }

    /**
     * 子类继承服务
     */
    protected open fun getService(): Service {
        return service;
    }
}