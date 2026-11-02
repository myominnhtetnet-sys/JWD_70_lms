package com.Learning_Managnment_System.JWD_70_lms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("LMS - Password Reset Verification Code");
        message.setText("Your password reset verification code is: " + code + "\nThis code will expire in 5 minutes.");
        
        mailSender.send(message);
    }
}