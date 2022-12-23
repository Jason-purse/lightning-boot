package com.jianyue.lightning.boot.starter.exception.dao;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 15:27
 * @Description 数据操作异常
 */
public class DefaultDataOperationException extends AbstractDaoException {

    private final String operationMethod;

    public DefaultDataOperationException(String message, String operationMethod) {
        super(DaoExStatusConstant.DATA_OPERATION_EXCEPTION_CONSTANT.value(), message);
        this.operationMethod = operationMethod;
    }

    @Override
    public String toString() {
        return "illegal data operation, operation method is [" + operationMethod + "],cause reason: [" + getMessage() + "]";
    }
}
