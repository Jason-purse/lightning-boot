package com.jianyue.lightning.boot.starter.feign;

import com.jianyue.lightning.boot.starter.exception.feign.DefaultFeignApplicationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * internal服务异常切面，抛出feignException
 *
 * @author WangMingLiang
 * @date 2021/1/6 13:40
 */
@Aspect
public class FeignExceptionAspect {

    @Around("execution(* *..internal.controller.*.*(..))")
    public Object around(ProceedingJoinPoint point) {
        Object result;
        try {
            result = point.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            throw DefaultFeignApplicationException.of(e.getMessage());
        }
        return result;
    }

}
