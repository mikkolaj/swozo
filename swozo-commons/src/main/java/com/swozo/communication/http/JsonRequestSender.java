package com.swozo.communication.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.jsonmapper.JsonMapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Component
public class JsonRequestSender implements RequestSender {
    private final JsonMapperFacade mapper;
    private final HttpClient httpClient;

    @Autowired
    public JsonRequestSender(JsonMapperFacade mapper) {
        this.mapper = mapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type) {
        return sendGet(uri, type, Function.identity());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(
            URI uri,
            TypeReference<T> type,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    ) {
        var request = builderDecorator.apply(
                HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
        ).build();

        return sendRequest(request, type);
    }

    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(
            URI uri,
            ReqBody body,
            TypeReference<RespBody> type
    ) {
        return sendPost(uri, body, type, Function.identity());
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(
            URI uri,
            ReqBody body,
            TypeReference<RespBody> type,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    ) {
        var request = builderDecorator.apply(
                requestToUriWithJsonHeaders(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.toJson(body)))
        ).build();

        return sendRequest(request, type);
    }

    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPut(
            URI uri,
            ReqBody body,
            TypeReference<RespBody> type
    ) {
        return sendPut(uri, body, type, Function.identity());
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPut(
            URI uri,
            ReqBody body,
            TypeReference<RespBody> type,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    ) {
        var request = builderDecorator.apply(
                requestToUriWithJsonHeaders(uri)
                        .PUT(HttpRequest.BodyPublishers.ofString(mapper.toJson(body)))
        ).build();

        return sendRequest(request, type);
    }

    private <T> CompletableFuture<HttpResponse<T>> sendRequest(HttpRequest request, TypeReference<T> type) {
        return httpClient.sendAsync(
                request,
                responseInfo -> HttpResponse.BodySubscribers.mapping(
                        HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                        json -> type.getType().equals(Void.class) ? null : mapper.fromJson(json, type)
                )
        );
    }

    private HttpRequest.Builder requestToUriWithJsonHeaders(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }
}
