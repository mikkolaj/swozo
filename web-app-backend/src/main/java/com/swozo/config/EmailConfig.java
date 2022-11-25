package com.swozo.config;

import com.swozo.email.EmailSender;
import com.swozo.email.MockEmailSender;
import com.swozo.email.SpringEmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {
    @Value("${" + EnvNames.SENDER_EMAIL + "}")
    private final String senderEmail;
    private final JavaMailSender sender;

    @Bean
    public EmailSender mockEmailSender() {
        return new MockEmailSender();
    }

    @Bean
    @Primary
    @Profile("prod")
    public EmailSender provideEmailSender() {
        return new SpringEmailSender(senderEmail, sender);
    }
}
