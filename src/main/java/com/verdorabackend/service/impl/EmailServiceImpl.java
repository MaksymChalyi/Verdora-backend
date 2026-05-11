package com.verdorabackend.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.verdorabackend.config.AppProperties;
import com.verdorabackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final AppProperties appProperties;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = appProperties.getFrontendUrl() + "/reset-password?token=" + token;

        Email from = new Email(appProperties.getMailFrom());
        Email toEmail = new Email(to);
        String subject = "Password Reset - Verdora";
        Content content = new Content("text/plain",
                "Hello!\n\n"
                        + "You requested a password reset.\n\n"
                        + "Click the link below to reset your password (valid for 15 minutes):\n"
                        + resetLink + "\n\n"
                        + "If you did not request this, please ignore this email.\n\n"
                        + "Verdora Team");

        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(appProperties.getSendgridApiKey());
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info("SendGrid response status: {}", response.getStatusCode());
            log.info("SendGrid response body: {}", response.getBody());
        } catch (IOException e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
