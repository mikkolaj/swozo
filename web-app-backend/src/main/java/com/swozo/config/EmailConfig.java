package com.swozo.config;

import com.swozo.mails.EmailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Bean
    public EmailSender provideEmailSender() {
        return new EmailSender();
    }
}
