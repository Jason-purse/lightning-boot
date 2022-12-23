package com.jianyue.lightning.boot.autoconfigure.web;

import com.jianyue.lightning.boot.starter.web.config.LightningWebConfigProperties;
import com.jianyue.lightning.boot.starter.web.config.WebConfigAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * @author FLJ
 * @date 2022/12/20
 * @time 14:34
 * @Description web auto configure
 *
 * 仅仅存在一个 web config properties 才进行 Web配置 ...
 */
@ConditionalOnClass(LightningWebConfigProperties.class)
@Import(WebConfigAutoConfiguration.class)
public class WebAutoConfigure {

}
