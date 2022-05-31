package com.swozo.security.keys;

import org.springframework.stereotype.Component;

@Component
public interface KeyProvider {
    byte[] getJwtSecretKey();
}
