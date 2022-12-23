package com.jianyue.lightning.boot.starter.util.mail;

import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.lang3.StringUtils;
import org.springside.modules.utils.collection.CollectionUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 邮件发送统一的通用的入口类
 *
 * @author zhangyang
 */
public class MailSenderTemplate {

    private MailConfig mailConfig;

    public MailSenderTemplate(MailConfig mailConfig) {
        this.mailConfig = mailConfig;
    }

    private  void mailSsl(Properties props) {
        final String sslFactory = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.debug", "false");
        props.put("mail.smtp.host", mailConfig.getSmtpHost());
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", sslFactory);
        props.put("mail.smtp.port", mailConfig.getSmtpPort());
        props.put("mail.smtp.socketFactory.port", mailConfig.getSmtpPort());
        props.put("mail.smtp.auth", "true");
    }
    public  boolean mailSender(List<String> listTo, List<String> listCc, String content, String subject) {
        Properties props = new Properties();
        boolean IsSend = true;
        //选择ssl方式
        mailSsl(props);
        Session session = Session.getDefaultInstance(props,new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConfig.getAccount(), mailConfig.getPassword());
            }
        });
        // -- Create a new message --
        MimeMessage msg = new MimeMessage(session);
        // -- Set the FROM and TO fields --
        try {
            msg.setFrom(new InternetAddress(mailConfig.getAccount()));
            /*邮件发送*/
            msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(StringUtils.join(listTo, ",")));
            /*邮件抄送*/
            if(CollectionUtil.isNotEmpty(listCc)){
                msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(StringUtils.join(listCc, ",")));
            }
            toSendMail(content, subject, session, msg);
        } catch (Exception e) {
            IsSend = false;
            System.out.println("邮件发送异常");
            e.printStackTrace();
        }
        return IsSend;
    }

    private void toSendMail(String content, String subject, Session session, MimeMessage msg) throws MessagingException {
        msg.setSubject(subject);
        msg.setContent(content, "text/html;charset=utf-8");
        msg.setSentDate(new Date());
        SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
        transport.connect();
        transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
        transport.close();
    }
}
