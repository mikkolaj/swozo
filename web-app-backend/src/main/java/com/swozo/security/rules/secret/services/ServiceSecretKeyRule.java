package com.swozo.security.rules.secret.services;

import com.swozo.security.keys.KeyProvider;
import com.swozo.security.rules.secret.SecretKeyRule;
import com.swozo.util.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequiredArgsConstructor
public class ServiceSecretKeyRule extends SecretKeyRule<ServiceType> {
    private final static String SERVICE_SECRET_KEY_HEADER = HttpHeaders.AUTHORIZATION;
    private final KeyProvider keyProvider;
    private final ServiceType serviceType;

    @Override
    protected byte[] extractSecretFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(SERVICE_SECRET_KEY_HEADER))
                .map(key -> key.getBytes(keyProvider.getKeyCharset()))
                .orElse(null);
    }

    @Override
    protected byte[] provideSecretKey() {
        return keyProvider.getServiceSecretKey(serviceType);
    }

    @Override
    protected ServiceType providePrincipalInfo() {
        return getService();
    }

    public ServiceType getService() {
        return serviceType;
    }
}
