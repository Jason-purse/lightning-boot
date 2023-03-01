package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.validates

import com.jianyue.lightning.boot.starter.generic.crud.service.util.ThreadLocalSupport
import org.springframework.util.Assert

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
interface ValidationSupport<S,T> {

    fun validationHandle(s: S): T


    companion object {

        private val state = ThreadLocalSupport.of<Class<out Validation>>()

        private val additionalState = ThreadLocalSupport.of<Any?>()

        /**
         * 设置验证组
         */
        fun getValidationGroup(): Class<out Validation>? {
            return state.get()
        }

        fun assertValidationGroup(): Class<out Validation> {
            val validationGroup: Class<out Validation?>? = getValidationGroup()

            Assert.isTrue(validationGroup != null, "validation group must not be null")

            assert(validationGroup != null)

            return validationGroup!!
        }

        /**
         * 并返回旧的
         */
        fun setValidationGroupAndReturnOld(validation: Class<out Validation>?): Class<out Validation>? {
            val old = state.get()
            if (validation == null) {
                state.remove()
            } else {
                state.set(validation)
            }
            return old
        }
        // --------------------------- aop 无法处理的情况下,需要切换上下文的便利使用方式 -------------------------

        fun setSelectListGroup() {
            setValidationGroupAndReturnOld(SELECT_LIST::class.java)
        }

        fun selectOneGroup() {
            setValidationGroupAndReturnOld(SELECT_ONE::class.java)
        }

        fun setSelectByIdGroup() {
            setValidationGroupAndReturnOld(SELECT_BY_ID::class.java)
        }

        fun setAddGroup() {
            setValidationGroupAndReturnOld(ADD::class.java)
        }

        fun setUpdateGroup() {
            setValidationGroupAndReturnOld(UPDATE::class.java)
        }

        fun setDeleteByIdGroup() {
            setValidationGroupAndReturnOld(DELETE_BY_ID::class.java)
        }

        fun setDeleteGroup() {
            setValidationGroupAndReturnOld(DELETE::class.java)
        }



        fun removeValidationGroupAndReturnOld(): Class<out Validation>? {
            val get = state.get()
            state.remove()
            return get
        }

        /**
         * 设置验证组
         */
        fun getAdditionalState(): Any? {
            return additionalState.get()
        }


        /**
         * 并返回旧的
         */
        fun setAdditionalStateAndReturnOld(state: Any?): Any? {
            val old = additionalState.get()
            if (state == null) {
                additionalState.remove()
            } else {
                additionalState.set(state)
            }
            return old
        }


        fun removeAdditionalStateAndReturnOld(): Any? {
            val get = additionalState.get()
            additionalState.remove()
            return get
        }

    }
}