package com.jianyue.lightning.exception;
/**
 * @author FLJ
 * @date 2022/12/20
 * @time 9:29
 * @Description 默认应用异常
 */
public class DefaultApplicationException extends AbstractApplicationException {

    public DefaultApplicationException(String message, Throwable throwable) {
        super(DefaultExceptionStatus.INFRASTRUCTURE_EXCEPTION_STATUS.value(), message, throwable);
    }

    public DefaultApplicationException(Integer code,String message,Throwable throwable) {
        super(code,message,throwable);
    }

    public static DefaultApplicationException of(String message) {
        return new DefaultApplicationException(message,null);
    }

    public static DefaultApplicationException of(Integer code,String message) {
        return new DefaultApplicationException(code,message,null);
    }

    public static DefaultApplicationException of(String message,Throwable ex) {
        return new DefaultApplicationException(message,ex);
    }
}
