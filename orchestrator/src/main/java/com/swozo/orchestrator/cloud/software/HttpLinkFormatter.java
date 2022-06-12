package com.swozo.orchestrator.cloud.software;

public class HttpLinkFormatter {
    private HttpLinkFormatter() {
    }

    private static final String HTTP_LINK_TEMPLATE = "http://%s:%s%s";

    public static String getLink(String ip, String port) {
        return String.format(HTTP_LINK_TEMPLATE, ip, port, "");
    }

    public static String getLink(String ip, String port, String resourcePath) {
        return String.format(HTTP_LINK_TEMPLATE, ip, port, resourcePath);
    }
}
