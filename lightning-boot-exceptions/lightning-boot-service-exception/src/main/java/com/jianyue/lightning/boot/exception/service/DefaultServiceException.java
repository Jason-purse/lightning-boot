package com.jianyue.lightning.boot.exception.service;

import com.jianyue.lightning.boot.exception.dao.AbstractDaoException;
import com.jianyue.lightning.exception.AbstractApplicationException;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 15:56
 * @Description 服务层异常 ...
 */
public class DefaultServiceException extends AbstractApplicationException {

    public DefaultServiceException(String message) {
        this(message, null);
    }

    /**
     * 对接 Dao层的异常
     *
     * @param message message
     * @param cause   导致的原因
     */
    public DefaultServiceException(String message, AbstractDaoException cause) {
        super(ServiceExStatusConstant.SERVICE_EX_STATUS_CONSTANT.value(), message, cause);
    }

}
