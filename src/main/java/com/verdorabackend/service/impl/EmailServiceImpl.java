package com.verdorabackend.service.impl;

import com.verdorabackend.config.AppProperties;
import com.verdorabackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = appProperties.getFrontendUrl() + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(appProperties.getMailFrom());
        message.setTo(to);
        message.setSubject("Password Reset - Verdora");
        message.setText("Hello!\n\n"
                + "You requested a password reset.\n\n"
                + "Click the link below to reset your password (valid for 15 minutes):\n"
                + resetLink + "\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Verdora Team");

        mailSender.send(message);
        log.info("Password reset email sent to: {}", to);
    }
}
