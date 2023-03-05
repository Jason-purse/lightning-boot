package com.jianyue.lightning.boot.starter.generic.crud.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "lightning.boot.generic.crud")
public class GenericCrudProperties {
    /**
     * 参数扫描包
     */
    private List<String> paramScanPages;
}
