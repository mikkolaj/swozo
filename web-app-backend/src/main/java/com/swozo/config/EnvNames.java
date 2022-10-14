package com.swozo.config;

public class EnvNames {
    public static final String REACT_DEV_SERVER_URL = "react.dev.server.url";

    public static final String JWT_SECRET_KEY = "jwt-secret-key";
    public static final String JWT_DEFAULT_EXPIRATION_SECONDS = "jwt-default-expiration-seconds";

    private static final String SERVICE_SECRET_KEY_SUFFIX = ".secret";
    public static final String ORCHESTRATOR_SECRET_KEY = "orchestrator" + SERVICE_SECRET_KEY_SUFFIX;
}
