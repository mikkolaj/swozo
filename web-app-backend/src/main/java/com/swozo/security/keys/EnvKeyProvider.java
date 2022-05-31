package com.swozo.security.keys;

import com.swozo.config.EnvNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvKeyProvider implements KeyProvider {
    @Value("${" + EnvNames.JWT_SECRET_KEY + "}")
    private String jwtSecretKey;

    @Override
    public byte[] getJwtSecretKey() {
        return jwtSecretKey.getBytes();
    }
}
