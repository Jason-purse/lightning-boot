package com.jianyue.lightning.boot.autoconfigure.mail;

import com.jianyue.lightning.framework.mail.MailConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhangyang
 */
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "lightning.mail")
@Data
public class MailProperties extends MailConfig {
}
