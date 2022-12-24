package com.jianyue.lightning.boot.exception.feign;

import com.jianyue.lightning.exception.ExceptionStatus;
/**
 * @author FLJ
 * @date 2022/12/20
 * @time 13:32
 * @Description feign 异常状态
 */
public class FeignExceptionStatus implements ExceptionStatus {

    private final String FEIGN_EXCEPTION_LABEL = "feign_label";

    private final String identity;

    private final Integer value;

    public FeignExceptionStatus(String identity,Integer code) {
        this.identity = identity;
        this.value = code;
    }

    @Override
    public String label() {
        return FEIGN_EXCEPTION_LABEL;
    }

    @Override
    public String identify() {
        return identity;
    }

    @Override
    public Integer value() {
        return value;
    }

    /**
     * 不管什么错误都已 400 状态码处理 ..
     */
    public final static  FeignExceptionStatus FEIGN_EXCEPTION_STATUS = new FeignExceptionStatus("feign_exception",400);
}
