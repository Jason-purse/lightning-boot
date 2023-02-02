package com.jianyue.lightning.exception;
/**
 * @author FLJ
 * @date 2022/12/20
 * @time 9:27
 * @Description 默认异常状态
 */
public class DefaultExceptionStatus implements ExceptionStatus {

    private final static String INFRASTRUCTURE_LABEL = "infrastructure";

    /**
     * 标识
     */
    private final String identify;

    /**
     * 值
     */
    private final Integer value;

    public DefaultExceptionStatus(String identify,Integer code) {

        this.identify = identify;
        this.value = code;

    }

    @Override
    public String label() {
        return INFRASTRUCTURE_LABEL;
    }

    @Override
    public String identify() {
        return identify;
    }

    @Override
    public Integer value() {
        return value;
    }


    /**
     * 应用中的默认底层基础设施  - 内部错误 ..
     */
    public final static DefaultExceptionStatus INFRASTRUCTURE_EXCEPTION_STATUS = new DefaultExceptionStatus("infrastructure_exception_error",500);
}
