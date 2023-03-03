package com.jianyue.lightning.framework.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MethodArgumentResolverConfiguration.class)
public class LightningWebConfigurations {
}
