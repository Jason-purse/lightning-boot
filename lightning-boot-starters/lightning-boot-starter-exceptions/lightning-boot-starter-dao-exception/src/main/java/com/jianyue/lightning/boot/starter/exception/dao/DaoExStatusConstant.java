package com.jianyue.lightning.boot.starter.exception.dao;

import com.jianyue.lightning.exception.ExceptionStatus;
/**
 * @author FLJ
 * @date 2022/12/19
 * @time 16:02
 * @Description DAO exception status constant
 */
public class DaoExStatusConstant implements ExceptionStatus {

    private final static String DAO_LABEL = "dao";

    private final String identity;

    private final Integer code;

    private DaoExStatusConstant(String identity,Integer code) {
        this.identity = identity;
        this.code = code;
    }

    @Override
    public String label() {
        return DAO_LABEL;
    }

    @Override
    public String identify() {
        return identity;
    }

    @Override
    public Integer value() {
        return code;
    }



    /**
     * 数据操作(crud)
     */
    public final static DaoExStatusConstant DATA_OPERATION_EXCEPTION_CONSTANT = new DaoExStatusConstant(
            "illegal_data_operation",
            500
    );
}
