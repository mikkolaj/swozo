package com.swozo.email;

import com.swozo.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailSender emailSender;

    public void sendResetPasswordEmail(User user) {
        // TODO i18n
        emailSender.send(new EmailData(
                user.getEmail(),
                "Swozo - Change password",
                "Copy this token: " + user.getChangePasswordToken()
        ));
    }
}
