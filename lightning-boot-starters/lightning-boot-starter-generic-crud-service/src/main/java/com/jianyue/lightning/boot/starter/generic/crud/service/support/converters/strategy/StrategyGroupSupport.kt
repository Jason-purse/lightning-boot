package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy

import com.jianyue.lightning.boot.starter.generic.crud.service.util.ThreadLocalSupport

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 14:25
 * @Description 验证支持
 *
 * 本质上它会被 设置,当它位于对应的验证组时 验证支持(用来让aop 缩小范围)
 * 并且,对应业务服务可以实现它,用于基本校验组策略分类
 *
 * 为了更方便的使用 通用验证组分组策略, 实现DefaultValidationSupportAdapter ..
 */
interface StrategyGroupSupport<S,T> {

    fun validationHandle(s: S): T


    companion object {

        private val state = ThreadLocalSupport.of<Class<out StrategyGroup>>()


        /**
         * 设置验证组
         */
        fun getValidationGroup(): Class<out StrategyGroup>? {
            return state.get()
        }

        /**
         * 并返回旧的
         */
        fun setStrategyGroupAndReturnOld(validation: Class<out StrategyGroup>?): Class<out StrategyGroup>? {
            val old = state.get()
            if (validation == null) {
                state.remove()
            } else {
                state.set(validation)
            }
            return old
        }
        // --------------------------- aop 无法处理的情况下,需要切换上下文的便利使用方式 -------------------------

        fun invokeForStrategyGroup(strategyGroup: Class<out StrategyGroup>,action: () -> Any?): Any? {
            return setStrategyGroupAndReturnOld(strategyGroup)
                    .run {
                        action().let {
                            setStrategyGroupAndReturnOld(this)
                            it
                        }
                    }
        }

        fun invokeForSelectList(action: () -> Any?): Any? {
            return invokeForStrategyGroup(SELECT_LIST::class.java,action)
        }

        fun invokeForSelectOne(action: () -> Any?): Any? {
            return invokeForStrategyGroup(SELECT_ONE::class.java, action)
        }

        fun invokeForSelectById(action: () -> Any?): Any? {
            return invokeForStrategyGroup(SELECT_BY_ID::class.java,action)
        }

        fun invokeForAdd(action: () -> Any?): Any? {
            return invokeForStrategyGroup(ADD::class.java,action)
        }

        fun invokeForUpdate(action: () -> Any?): Any? {
            return invokeForStrategyGroup(UPDATE::class.java, action)
        }

        fun invokeForDeleteById(action: () -> Any?) : Any?{
            return invokeForStrategyGroup(DELETE_BY_ID::class.java,action)
        }

        fun invokeForDelete(action: () -> Any?): Any? {
            return invokeForStrategyGroup(DELETE::class.java, action)
        }



        fun removeStrategyGroupAndReturnOld(): Class<out StrategyGroup>? {
            val get = state.get()
            state.remove()
            return get
        }


    }
}