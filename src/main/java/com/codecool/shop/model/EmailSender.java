package com.codecool.shop.model;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class EmailSender {

    private String toEmail ; //client Email
    private String subjectEmail ;
    private String messageEmail ;
    private static String userGmail="codecoolbucurestitest@gmail.com"; //server Email
    private static String pswGmail = "strlumina"; //server
    private static String host = "smtp.gmail.com"; //server

    public EmailSender(String toEmail, String subjectEmail, String messageEmail) {
        this.toEmail = toEmail;
        this.subjectEmail = subjectEmail;
        this.messageEmail = messageEmail;

        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(userGmail,pswGmail);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userGmail));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(toEmail));
            message.setSubject(subjectEmail);
            message.setText(messageEmail);

            Transport.send(message);

            System.out.println("E-mail sent successfully");


        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
