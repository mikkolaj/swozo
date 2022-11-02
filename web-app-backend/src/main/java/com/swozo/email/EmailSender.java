package com.swozo.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor
public class EmailSender {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String senderEmail;
    private final JavaMailSender sender;

    public void send(EmailData emailData) {
        var email = new SimpleMailMessage();

        email.setFrom(senderEmail);
        email.setTo(emailData.to());
        email.setSubject(emailData.subject());
        email.setText(emailData.body());

        logger.info("sending email {}", email);
        try {
            sender.send(email);
        } catch (MailException mailException) {
            logger.error("Failed to send email to " + emailData.to(), mailException);
            throw new EmailFailedException("Failed to send email to " + emailData.to());
        }
    }
}
