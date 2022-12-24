package com.jianyue.lightning.framework.web.advice;


import com.jianyue.lightning.boot.exception.service.DefaultServiceException;
import com.jianyue.lightning.boot.exception.web.DefaultIllegalArgumentException;
import com.jianyue.lightning.boot.exception.web.DefaultWebApplicationException;
import com.jianyue.lightning.boot.exception.web.WebExStatusConstant;
import com.jianyue.lightning.exception.AbstractApplicationException;
import com.jianyue.lightning.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 默认的全局异常处理handler
 *
 * @author FLJ
 */
public class DefaultGlobalControllerExceptionHandler {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 全局的处理 ..
     *
     * @param ex ex
     * @return 处理结果
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(AbstractApplicationException.class)
    @ResponseBody
    public Result<?> handleRootException(AbstractApplicationException ex) {
        logger.info("catch an exception,will print stack trace !!!");
        logger.info("--------------------------------------------------------------------");
        if (ex.getCause() != null) {
            ex.getCause().printStackTrace();
        } else {
            ex.printStackTrace();
        }
        logger.info("--------------------------------------------------------------------");
        return ex.asResult();
    }

    /**
     * service 层的异常处理
     *
     * @param ex ex
     * @return 处理结果
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(DefaultServiceException.class)
    @ResponseBody
    public Result<?> handleServiceException(DefaultServiceException ex) {
        return handleRootException(ex);
    }


    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    @ResponseBody
    public Result<?> handleBindException(BindException ex) {

        final BindingResult bindingResult = ex.getBindingResult();
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getField()).append("[")
                    .append(fieldError.getDefaultMessage())
                    .append("]")
                    .append("\n");
        }

        return handleRootException(new DefaultWebApplicationException(WebExStatusConstant.ARGUMENT_EXCEPTION_CONSTANT.value(), builder.toString()));
    }


    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String validErrorParams = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"));
        return handleRootException(new DefaultWebApplicationException(WebExStatusConstant.ARGUMENT_EXCEPTION_CONSTANT.value(), validErrorParams));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return handleBindException(e);
    }


    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return handleRootException(DefaultIllegalArgumentException.inValidArgumentEx(ex.getParameterName(), ex.getMessage()));
    }

    /**
     * 处理其他异常, 服务器内部异常
     *
     * @param ex ex
     * @return 解析结果 result
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Result<?> handleOtherException(Throwable ex) {
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
}
