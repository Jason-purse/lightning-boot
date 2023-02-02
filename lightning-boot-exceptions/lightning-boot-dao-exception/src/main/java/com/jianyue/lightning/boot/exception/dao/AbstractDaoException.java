package com.jianyue.lightning.boot.exception.dao;

import com.jianyue.lightning.exception.AbstractApplicationException;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 15:57
 * @Description 抽象的Dao层异常
 */
public class AbstractDaoException extends AbstractApplicationException {
    public AbstractDaoException(Integer code, String message) {
        super(code, message, null);
    }

}
