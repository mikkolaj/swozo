package com.swozo.communication.http;

import com.swozo.jsonmapper.JsonMapperFacade;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JsonRequestSender implements RequestSender {
    private final JsonMapperFacade mapper;

    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, Class<T> clazz) {
        return sendGet(uri, clazz, Function.identity());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(
            URI uri,
            Class<T> clazz,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    ) {
        var request = builderDecorator.apply(
                HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
        ).build();

        return sendRequest(request, clazz);
    }

    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(
            URI uri,
            ReqBody body,
            Class<RespBody> clazz
    ) {
        return sendPost(uri, body, clazz, Function.identity());
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(
            URI uri,
            ReqBody body,
            Class<RespBody> clazz,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    ) {
        var request = builderDecorator.apply(
                HttpRequest.newBuilder()
                        .uri(uri)
                        .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .headers(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.toJson(body)))
                ).build();

        return sendRequest(request, clazz);
    }

    private <T> CompletableFuture<HttpResponse<T>> sendRequest(HttpRequest request, Class<T> clazz) {
        return HttpClient.newBuilder()
                .build()
                .sendAsync(
                        request,
                        responseInfo -> HttpResponse.BodySubscribers.mapping(
                            HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                            json -> clazz.equals(Void.class) ? null : mapper.fromJson(json, clazz)
                   )
                );
    }
}
