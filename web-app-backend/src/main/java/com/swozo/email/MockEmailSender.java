package com.swozo.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockEmailSender implements EmailSender {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void send(EmailData emailData) {
        logger.info("Mock email: {}", emailData);
    }
}
