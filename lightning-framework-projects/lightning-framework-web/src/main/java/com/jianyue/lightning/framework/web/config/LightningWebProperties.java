package com.jianyue.lightning.framework.web.config;

import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * lightning web properties
 */
@Data
@ConfigurationProperties(prefix = "lightning.framework.web")
public class LightningWebProperties {

    // junit 单元测试可能会产生奇怪的命令 com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 com.generatera.usercenter.authorization.proxy.test.AuthorizationProxyApplicationTests,test
    private final String defaultMainApplicationClass = System.getProperty("sun.java.command");
    /**
     * 可以配置
     */
    private String mainApplicationClass;

    public String getMainApplicationClass() {
        return ElvisUtil.stringElvis(mainApplicationClass,defaultMainApplicationClass);
    }
}
