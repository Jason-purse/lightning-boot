package com.jianyue.lightning.result;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 16:34
 * @Description 结果抽象
 */
public interface Result<T> {

    Integer getCode();

    String getMessage();

    /**
     * 是否存在result
     *
     * @return result check flag
     * @implNote result / results 没有互斥关系
     */
    boolean hasResult();

    /**
     * 是否存在results
     *
     * @return results check flag
     * @implNote result / results 没有互斥关系
     */
    boolean hasResults();

    /**
     * 获取Results
     *
     * @return list
     * @implNote 如果没有则返回null
     */
    T getResult();

    /**
     * 获取Result
     *
     * @return result
     * @implNote 如果没有则返回null
     */
    List<T> getResults();


    public static Result<Void> success(Integer code, String message) {
        return new DefaultResultImpl<>(code, message, null, null);
    }

    public static <T> Result<T> success(Integer code, String message, T result) {
        return new DefaultResultImpl<>(code, message, null, result);
    }

    public static <T> Result<T> success(Integer code, String message, List<T> results) {
        return new DefaultResultImpl<>(code, message, results, null);
    }

    public static Result<Void> error(Integer code, String message) {
        return new DefaultResultImpl<>(code, message, null, null);
    }

    /**
     * 通用的处理 ...
     *
     * @param code    code
     * @param message message
     * @param results result
     * @param result  result
     * @param results results
     * @param <T>     T
     * @return result
     */
    public static <T> Result<T> of(Integer code, String message, List<T> results, T result) {
        return new DefaultResultImpl<>(code, message, results, result);
    }

    public static Class<?> getDefaultImplementClass() {
        return DefaultResultImpl.class;
    }

}
