package com.swozo.api.common.email;

import com.swozo.mails.EmailSender;
import com.swozo.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailSender emailSender;

    public void sendChangePasswordEmail(User user) {

    }
}
