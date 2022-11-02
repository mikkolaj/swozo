package com.swozo.config;

import com.swozo.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailConfig {
    private final String senderEmail;
    private final JavaMailSender sender;

    @Autowired
    public EmailConfig(@Value("${" + EnvNames.SENDER_EMAIL + "}") String senderEmail, JavaMailSender sender) {
        this.senderEmail = senderEmail;
        this.sender = sender;
    }

    @Bean
    public EmailSender provideEmailSender() {
        return new EmailSender(senderEmail, sender);
    }
}
