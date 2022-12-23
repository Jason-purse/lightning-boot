package com.jianyue.lightning.boot.starter.util.mail;

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
