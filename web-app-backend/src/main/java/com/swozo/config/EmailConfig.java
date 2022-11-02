package com.swozo.config;

import com.swozo.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {
    private final JavaMailSender sender;

    @Bean
    public EmailSender provideEmailSender() {
        return new EmailSender(sender);
    }
}
