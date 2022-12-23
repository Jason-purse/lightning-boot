package com.jianyue.lightning.boot.starter.exception.web;

import com.jianyue.lightning.exception.ExceptionStatus;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 15:13
 * @Description web exception status constant
 */
public class WebExStatusConstant implements ExceptionStatus {
    public final static String WEB_LABEL = "web";

    private final String identity;

    private final Integer code;

    private WebExStatusConstant(String identity, Integer code) {
        this.identity = identity;
        this.code = code;
    }

    @Override
    public String label() {
        return WEB_LABEL;
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
     * 参数异常
     */
    public final static WebExStatusConstant ARGUMENT_EXCEPTION_CONSTANT = new WebExStatusConstant(
            "argument_illegal",
            400
    );

}
