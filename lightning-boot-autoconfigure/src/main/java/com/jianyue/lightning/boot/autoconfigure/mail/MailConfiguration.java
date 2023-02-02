package com.jianyue.lightning.boot.autoconfigure.mail;

import com.jianyue.lightning.framework.mail.MailSenderTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Mail配置
 *
 * @author konghang
 */
@ConditionalOnClass(MailSenderTemplate.class)
@EnableConfigurationProperties(MailProperties.class)
public class MailConfiguration {

    @Bean
    public MailSenderTemplate mailSenderTemplate(MailProperties mailProperties){
        return new MailSenderTemplate(mailProperties);
    }
}
