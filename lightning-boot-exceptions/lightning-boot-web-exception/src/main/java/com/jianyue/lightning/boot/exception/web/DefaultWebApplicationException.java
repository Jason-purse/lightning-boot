package com.jianyue.lightning.boot.exception.web;

import com.jianyue.lightning.exception.AbstractApplicationException;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 15:02
 * @Description 默认实现的应用级别的异常
 *  web 应用,不需要将底层的异常信息抛出到外部 ..
 */
public class DefaultWebApplicationException extends AbstractApplicationException {

    public DefaultWebApplicationException(Integer code, String message) {
        super(code, message, null);
    }
}
