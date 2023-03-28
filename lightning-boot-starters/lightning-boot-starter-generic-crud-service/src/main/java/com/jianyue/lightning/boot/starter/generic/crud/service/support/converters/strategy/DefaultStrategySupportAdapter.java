package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy;

/**
 * @author FLJ
 * @date 2022/12/12
 * @time 11:38
 * @Description 实现接口
 *
 * 它自己本身不进行校验处理 !!!
 * 通过{@link org.springframework.web.method.annotation.ModelAttributeMethodProcessor} 进行 controller 中的方法
 * 注入的验证组来进行 进行查询分类验证 !!!
 */
public interface DefaultStrategySupportAdapter<S, T> extends StrategyGroupSupport<S, T> {


    default T addGroupHandle(S s) {
        return null;
    }

    default T selectListGroupHandle(S s) {
        return null;
    }
    default T selectOneGroupHandle(S s) { return null; }
    default T selectByIdGroupHandle(S s) {
        return null;
    }

    default T updateGroupHandle(S s) {
        return null;
    }

    default T deleteGroupHandle(S s) {
        return null;
    }

    default T deleteByIdGroupHandle(S s) {
        return null;
    }


    default T validationHandle(S s) {
        Class<? extends StrategyGroup> validationGroup = StrategyGroupSupport.Companion.getValidationGroup();
        if (ADD.class.equals(validationGroup)) {
            return addGroupHandle(s);
        } else if (SELECT_BY_ID.class.equals(validationGroup)) {
            return selectByIdGroupHandle(s);
        } else if (SELECT_LIST.class.equals(validationGroup)) {
            return selectListGroupHandle(s);
        } else if(SELECT_ONE.class.equals(validationGroup)) {
            return selectOneGroupHandle(s);
        }
        else if (UPDATE.class.equals(validationGroup)) {
            return updateGroupHandle(s);
        }
        else if (DELETE.class.equals(validationGroup)) {
            return deleteGroupHandle(s);
        } else if (DELETE_BY_ID.class.equals(validationGroup)) {
            return deleteByIdGroupHandle(s);
        } else {
            // 给其他转换器一个机会 !!!
            return null;
            //throw new IllegalArgumentException("can't not support current validation Group, please override this method !!!");
        }
    }


}
