package com.swozo.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Component
public class RequestSender {
    public CompletableFuture<HttpResponse<String>> sendGet(URI uri) {
        var request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        return sendRequest(request);
    }

    public CompletableFuture<HttpResponse<String>> sendPost(URI uri, String jsonInputString) {
        var request = HttpRequest.newBuilder()
                .uri(uri)
                .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();

        return sendRequest(request);
    }

    private CompletableFuture<HttpResponse<String>> sendRequest(HttpRequest request) {
        return HttpClient.newBuilder()
                .build()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
