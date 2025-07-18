package com.example.peopoolbe.mail.service;

import com.example.peopoolbe.global.redis.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final String senderEmail;
    private final RedisUtil redisUtil;

    public MailService(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String senderEmail, RedisUtil redisUtil) {
        this.javaMailSender = javaMailSender;
        this.senderEmail = senderEmail;
        this.redisUtil = redisUtil;
    }

    private String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }

        return key.toString();
    }

    private MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>이메일 인증 번호</h3>";
        body += "<h1>" + number + "</h1>";
        message.setText(body, "utf-8", "html");

        return message;
    }

    public String sendCodeMessage(String senderEmail) throws MessagingException {
        String number = createNumber();

        MimeMessage message = createMail(senderEmail, number);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다.");
        }
        redisUtil.setDataExpire(number, senderEmail, 60*5L);

        return number;
    }

    public boolean checkCode(String email, String code) {
        if(redisUtil.getData(code) == null) {
            return false;
        }
        else if(redisUtil.getData(code).equals(email)) {
            return true;
        }
        else return false;
    }
}
