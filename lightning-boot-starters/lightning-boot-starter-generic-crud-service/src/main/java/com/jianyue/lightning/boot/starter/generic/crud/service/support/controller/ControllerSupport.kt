package com.jianyue.lightning.boot.starter.generic.crud.service.support.controller

import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple
import com.jianyue.lightning.boot.starter.util.ThreadLocalSupport

/**
 * @author FLJ
 * @date 2022/12/13
 * @time 11:40
 * @Description 控制器支持
 */
interface ControllerSupport {

    companion object {
        /**
         * 新的,旧的
         */
        val paramClassState = ThreadLocalSupport.of<Tuple<Class<*>, Any>?>()

    }
}