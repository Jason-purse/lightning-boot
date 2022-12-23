package com.jianyue.lightning.boot.starter.exception.feign;

import com.jianyue.lightning.exception.AbstractApplicationException;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 13:30
 * @Description 抽象的 feign 应用异常
 *
 * feign exception ..
 */
public abstract class AbstractFeignApplicationException  extends AbstractApplicationException {
    public AbstractFeignApplicationException(Integer code, String message, Throwable throwable) {
        super(code, message, throwable);
    }


}
