package com.swozo.config;

public class EnvNames {
    public static final String REACT_DEV_SERVER_URL = "react.dev.server.url";

    public static final String JWT_SECRET_KEY = "jwt-secret-key";
    public static final String JWT_DEFAULT_EXPIRATION_SECONDS = "jwt-default-expiration-seconds";
    public static final String REFRESH_TOKEN_EXPIRATION_SECONDS = "refresh-token-default-expiration-seconds";

    public static final String SENDER_EMAIL = "email.sender";

    private static final String SERVICE_SECRET_KEY_SUFFIX = ".secret";
    public static final String ORCHESTRATOR_SECRET_KEY = "orchestrator" + SERVICE_SECRET_KEY_SUFFIX;
    public static final String SERVICE_CONFIG_CACHE_REVALIDATE_DURATION = "orchestrator.cache.revalidate.serviceConfig";
}
