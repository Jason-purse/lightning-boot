package com.jianyue.lightning.exception;

import com.jianyue.lightning.result.Result;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 14:56
 * @Description 抽象的应用级异常
 */
public abstract class AbstractApplicationException extends LightningException {

    private final Integer code;

    public AbstractApplicationException(Integer code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }


    public Result<?> asResult() {
        return Result.error(code, getMessage());
    }

    @Override
    public String toString() {
        return "application exception: code = " + code + ", message= " + getMessage();
    }
}
