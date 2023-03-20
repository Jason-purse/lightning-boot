package com.jianyue.lightning.framework.web.config;

import com.jianyue.lightning.framework.web.method.argument.resolver.strategy.FactoryStrategyHandlerMethodArgumentResolver;
import com.jianyue.lightning.framework.web.method.argument.resolver.strategy.HandlerMethodArgumentFactoryStrategyResolver;
import com.jianyue.lightning.framework.web.method.argument.resolver.strategy.SpiHandlerMethodArgumentStrategyResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@EnableConfigurationProperties(LightningWebProperties.class)
public class LightningWebConfigurations {

    @Bean
    public FactoryStrategyHandlerMethodArgumentResolver factoryStrategyHandlerMethodArgumentResolver() {
        return new FactoryStrategyHandlerMethodArgumentResolver();
    }

    @Bean
    public SpiHandlerMethodArgumentStrategyResolver spiHandlerMethodArgumentStrategyResolver() {
        return new SpiHandlerMethodArgumentStrategyResolver();
    }


}
