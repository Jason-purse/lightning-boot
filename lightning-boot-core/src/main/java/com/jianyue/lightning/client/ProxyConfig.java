package com.jianyue.lightning.client;

import lombok.Data;

/**
 * 代理配置
 * @author konghang
 */
@Data
public class ProxyConfig {
    private Boolean isProxy = Boolean.FALSE;
    private String host;
    private Integer port;
}
