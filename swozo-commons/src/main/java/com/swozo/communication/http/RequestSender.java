package com.swozo.communication.http;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface RequestSender {
    <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, Class<T> clazz);

    <T> CompletableFuture<HttpResponse<T>> sendGet(
            URI uri,
            Class<T> clazz,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    );

    <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, Class<RespBody> clazz);

    <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(
            URI uri,
            ReqBody body,
            Class<RespBody> clazz,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    );

    static <T> CompletableFuture<T> unwrap(CompletableFuture<HttpResponse<T>> response) {
        return response.thenApply(HttpResponse::body);
    }
}
