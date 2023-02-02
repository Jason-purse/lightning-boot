package com.jianyue.lightning.boot.exception.web;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 15:03
 * @Description 默认的参数异常
 */
public class DefaultIllegalArgumentException extends DefaultWebApplicationException {
    /**
     * 参数名称
     */
    private final String argumentName;

    public DefaultIllegalArgumentException(String argumentName, String message) {
        super(WebExStatusConstant.ARGUMENT_EXCEPTION_CONSTANT.value(), message);
        this.argumentName = argumentName;
    }

    public static DefaultIllegalArgumentException of(String argumentName,String message) {
        return new DefaultIllegalArgumentException(argumentName,message);
    }

    public String getArgumentName() {
        return argumentName;
    }

    @Override
    public String toString() {
        return "illegal argument [" + argumentName + "],cause reason: [" + getMessage() + "]";
    }


    // 无效参数
    public static DefaultIllegalArgumentException inValidArgumentEx(String argumentName,String message) {
        return new DefaultIllegalArgumentException(argumentName,message);
    }

    public static DefaultIllegalArgumentException requiredArgumentEx(String argumentName,String message) {
        return new DefaultIllegalArgumentException(argumentName,message);
    }

    public static DefaultIllegalArgumentException defaultRequiredArgumentEx(String argumentName) {
        return new DefaultIllegalArgumentException(argumentName,"This parameter is required !");
    }
    public static DefaultIllegalArgumentException defaultInvalidArgumentEx(String argumentName) {
        return new DefaultIllegalArgumentException(argumentName,"This parameter is not valid !");
    }

    public static DefaultIllegalArgumentException requiredArgumentExForChina(String argumentName) {
        return new DefaultIllegalArgumentException(argumentName,"此参数是必须的 !");
    }
    public static DefaultIllegalArgumentException invalidArgumentExForChina(String argumentName) {
        return new DefaultIllegalArgumentException(argumentName,"此参数是无效的 !");
    }
}
