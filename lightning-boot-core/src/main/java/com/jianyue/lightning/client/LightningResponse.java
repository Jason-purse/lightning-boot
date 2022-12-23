package com.jianyue.lightning.client;

import lombok.Builder;
import lombok.Data;

/**
 * 响应对象类
 *
 * 同okhttp比较仅返回比较有用的信息，
 * 且将连接释放回连接池供其他连接使用
 * @author konghang
 */
@Data
public class LightningResponse {

    /**
     * 是否成功
     *
     * @return
     */
    public boolean isSuccessful() {
        return this.code >= 200 && this.code < 300;
    }

    /**
     * http状态码
     */
    private int code;

    /**
     * 响应体
     */
    private String body;

    @Builder
    public LightningResponse(int code, String body){
        this.code = code;
        this.body = body;
    }
}
