package com.jianyue.lightning.boot.starter.generic.crud.service.support.validates;

/**
 * @author FLJ
 * @date 2022/12/12
 * @time 11:38
 * @Description 实现接口
 */
public interface DefaultValidationSupportAdapter<S, T> extends ValidationSupport<S, T> {


    default T addGroupHandle(S s) {
        return null;
    }

    default T selectListGroupHandle(S s) {
        return null;
    }

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
        Class<? extends Validation> validationGroup = ValidationSupport.Companion.getValidationGroup();
        if (ADD.class.equals(validationGroup)) {
            return addGroupHandle(s);
        } else if (SELECT_BY_ID.class.equals(validationGroup)) {
            return selectByIdGroupHandle(s);
        } else if (SELECT_LIST.class.equals(validationGroup)) {
            return selectListGroupHandle(s);
        } else if (UPDATE.class.equals(validationGroup)) {
            return updateGroupHandle(s);
        } else if (DELETE.class.equals(validationGroup)) {
            return deleteGroupHandle(s);
        } else if (DELETE_BY_ID.class.equals(validationGroup)) {
            return deleteByIdGroupHandle(s);
        } else {
            throw new IllegalArgumentException("can't not support current validation Group, please override this method !!!");
        }
    }


}
