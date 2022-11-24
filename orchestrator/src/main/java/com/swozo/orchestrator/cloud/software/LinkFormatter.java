package com.swozo.orchestrator.cloud.software;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LinkFormatter {
    private static final String HTTP_PROTOCOL = "http";
    private static final String HTTPS_PROTOCOL = "https";
    private static final String LINK_TEMPLATE = "%s://%s:%s%s";

    public String getHttpLink(String ip, String port) {
        return getHttpLink(ip, port, "");
    }

    public String getHttpLink(String ip, String port, String resourcePath) {
        return getLink(HTTP_PROTOCOL, ip, port, resourcePath);
    }

    public String getHttpsLink(String ip, String port) {
        return getHttpsLink(ip, port, "");
    }

    public String getHttpsLink(String ip, String port, String resourcePath) {
        return getLink(HTTPS_PROTOCOL, ip, port, resourcePath);
    }

    public String getLink(String protocol, String ip, String port, String resourcePath) {
        return String.format(LINK_TEMPLATE, protocol, ip, port, resourcePath);
    }

    public String appendQueryParams(String link, Map<String, String> queryParams) {
        return link + "?" +
                queryParams.entrySet().stream()
                    .map(param -> param.getKey() + "=" + param.getValue())
                    .collect(Collectors.joining("&"));
    }
}
