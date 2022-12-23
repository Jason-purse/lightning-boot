package com.jianyue.lightning.boot.starter.exception.feign;
/**
 * @author FLJ
 * @date 2022/12/20
 * @time 13:35
 * @Description 默认的Feign 应用异常 ..
 */
public class DefaultFeignApplicationException extends AbstractFeignApplicationException {
    public DefaultFeignApplicationException(String message, Throwable throwable) {
        super(FeignExceptionStatus.FEIGN_EXCEPTION_STATUS.value(), message, throwable);
    }

    public static DefaultFeignApplicationException of(String message) {
        return new DefaultFeignApplicationException(message,null);
    }
}
