package com.volvo.gloria.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import com.icegreen.greenmail.user.UserException;

public class MailSenderTest {

    private static final String EMAIL_TO = "someone@localhost.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail.";
    private static final String LOCALHOST = "127.0.0.1";
    
    GreenMail mailServer;
    @Before
    public void before(){
        mailServer = new GreenMail(ServerSetupTest.SMTP);
        mailServer.start(); 
    }

    @Test
    public void getMails() throws IOException, MessagingException, UserException, InterruptedException {

        MailSender sender = new MailSender();
        sender.setHost(LOCALHOST);
        //this is the port on which GreenMail listens to. 
        sender.setPort(3025);
        sender.sendMail(EMAIL_TO, EMAIL_SUBJECT, EMAIL_TEXT);

        // fetch messages from server
        MimeMessage[] messages = mailServer.getReceivedMessages();
        assertNotNull(messages);
        assertEquals(1, messages.length);
        MimeMessage m = messages[0];
        assertEquals(EMAIL_SUBJECT, m.getSubject());
        assertTrue(String.valueOf(m.getContent()).contains(EMAIL_TEXT));
    }
    
    @After
    public void after(){
        mailServer.stop();
    }  
}
