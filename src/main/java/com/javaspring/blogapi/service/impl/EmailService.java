package com.javaspring.blogapi.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${domain.name}")
    private String domainUrl;

    public void sendMail(String toEmail,String verifyCode) throws MessagingException {
        String body = String.format("Gửi bạn %s, </br>" +
                "Vui lòng nhấp vào link bên dưới để xác thực </br>" +
                "<h3><a href=\"%s/auth/verify-email?verify_code=%s\">XÁC THỰC</a></h3>" +
                "Cảm ơn.", toEmail, domainUrl, verifyCode);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("verifyemail.java@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject("Vui lòng xác minh email để kích hoạt tài khoản");
        helper.setText(body, true);

        javaMailSender.send(message);

        System.out.println("Send mail to: " + toEmail);
    }

    public String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
