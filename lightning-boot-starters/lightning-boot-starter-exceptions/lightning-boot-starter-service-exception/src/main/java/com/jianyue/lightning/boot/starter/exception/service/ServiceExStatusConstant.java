package com.jianyue.lightning.boot.starter.exception.service;

import com.jianyue.lightning.exception.ExceptionStatus;
/**
 * @author FLJ
 * @date 2022/12/19
 * @time 16:07
 * @Description 服务层异常状态约束
 */
public class ServiceExStatusConstant implements ExceptionStatus {

    private final static String SERVICE_LABEL = "service";


    private final String identity;

    private final Integer code;

    private ServiceExStatusConstant(String identity,Integer code) {
        this.identity = identity;
        this.code = code;
    }

    @Override
    public String label() {
        return SERVICE_LABEL;
    }

    @Override
    public String identify() {
        return identity;
    }

    @Override
    public Integer value() {
        return code;
    }


    public final static ServiceExStatusConstant SERVICE_EX_STATUS_CONSTANT = new ServiceExStatusConstant(
            "generic_service_exception",
            400
    );

}
