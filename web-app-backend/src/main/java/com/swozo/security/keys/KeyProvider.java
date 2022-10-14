package com.swozo.security.keys;

import com.swozo.util.ServiceType;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
public interface KeyProvider {
    byte[] getJwtSecretKey();

    byte[] getServiceSecretKey(ServiceType serviceType);

    default Charset getKeyCharset() {
        return StandardCharsets.US_ASCII;
    }
}
