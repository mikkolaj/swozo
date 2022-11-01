package com.swozo.mails;

public record EmailData(
        String receiverEmail,
        String header,
        String body
) {
}
