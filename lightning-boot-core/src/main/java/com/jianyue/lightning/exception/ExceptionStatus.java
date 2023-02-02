package com.jianyue.lightning.exception;

/**
 * @author FLJ
 * @date 2022/12/19
 * @time 15:10
 * @Description 异常状态码
 */
public interface ExceptionStatus {
    /**
     * 标签 / 分类
     * @return
     */
    public String label();

    /**
     * 异常码身份
     * @return
     */
    public String identify();


    /**
     * 异常码
     * @return
     */
    public Integer value();

}
