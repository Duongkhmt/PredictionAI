//package com.example.DuDoanAI.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Service
//public class EmailService {
//    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendResetPasswordEmail(String to, String token) {
//        try {
//            String resetUrl = "http://localhost:8082/reset-password?token=" + token;
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject("Đặt lại mật khẩu");
//            message.setText("Bấm vào liên kết để đổi mật khẩu: " + resetUrl);
//
//            mailSender.send(message);
//            logger.info("Email sent successfully to: {}", to);
//        } catch (Exception e) {
//            logger.error("Failed to send email to: {}", to, e);
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
//}

package com.example.DuDoanAI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String token) {
        try {
//            String resetUrl = "http://localhost:8082/reset-password?token=" + token;
            String resetUrl = "http://192.168.1.11:8082/reset-password?token=" + token;

            logger.info("Attempting to send email to: {}", to);
            logger.info("Reset URL: {}", resetUrl);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Đặt lại mật khẩu");
            message.setText("Bấm vào liên kết để đổi mật khẩu: " + resetUrl);

            // Log thông tin email
            logger.info("Email details - To: {}, Subject: {}", to, message.getSubject());

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}