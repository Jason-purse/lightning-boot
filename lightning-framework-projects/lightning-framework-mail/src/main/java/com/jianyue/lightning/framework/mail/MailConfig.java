package com.jianyue.lightning.framework.mail;

import lombok.Data;

/**
 * @author zhangyang
 */
@Data
public class MailConfig {

    private String smtpHost;

    private Integer smtpPort;

    private String account;

    private String password;
}
