package com.swozo.security.keys;

import com.swozo.config.EnvNames;
import com.swozo.utils.ServiceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvKeyProvider implements KeyProvider {
    @Value("${" + EnvNames.JWT_SECRET_KEY + "}")
    private String jwtSecretKey;
    @Value("${" + EnvNames.ORCHESTRATOR_SECRET_KEY + "}")
    private String orchestratorSecretKey;

    @Override
    public byte[] getJwtSecretKey() {
        return jwtSecretKey.getBytes();
    }

    @Override
    public byte[] getServiceSecretKey(ServiceType serviceType) {
        var key = switch (serviceType) {
            case ORCHESTRATOR -> orchestratorSecretKey;
            default -> throw new IllegalStateException("Secret key isn't configured for " + serviceType);
        };

        return key.getBytes(getKeyCharset());
    }
}
