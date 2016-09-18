package com.volvo.gloria.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.volvo.gloria.config.b.beans.ApplicationProperties;
import com.volvo.jvs.runtime.servicesupport.ServiceLocator;

public class MailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
    
    private String environment = "";

    public MailSender() {
        try {
            ApplicationProperties applicationProperties = ServiceLocator.getService(ApplicationProperties.class);
            String mailHost = applicationProperties.getMailHost();
            this.setHost(mailHost); 
            this.setEnvironment(applicationProperties.getEnvironment());
        } catch (Exception mailException) {
            LOGGER.warn("Unable to sennd Mail in constructor : " + mailException.getMessage(), mailException);
        }
    }
    /*
     * needed for test cases to set a port where this can run
     */
    public void setPort(int port) {
        javaMailSenderImpl.setPort(port);
    }
    /*
     * needed to set host to localhost from testcases
     */
    public void setHost(String host) {
        javaMailSenderImpl.setHost(host);
    }

    public void sendMail(String toMailAddress, String messageTitle, String messageContent) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("gloria@volvo.com");
        msg.setSubject(messageTitle);
        msg.setTo(toMailAddress);
        msg.setText(messageContent);
        try {
            javaMailSenderImpl.send(msg);
        } catch (MailException mailException) {
            //ignoring Exception for functionality to continue
            LOGGER.warn("Unable to sennd Mail : " + mailException.getMessage(), mailException);
        }
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public String getEnvironment() {
        return environment;
    }
}
