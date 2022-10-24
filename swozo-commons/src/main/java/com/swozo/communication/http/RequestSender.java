package com.swozo.communication.http;

import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface RequestSender {
    <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type);

    <T> CompletableFuture<HttpResponse<T>> sendGet(
            URI uri,
            TypeReference<T> type,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    );

    <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(
            URI uri,
            ReqBody body,
            TypeReference<RespBody> type
    );

    <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(
            URI uri,
            ReqBody body,
            TypeReference<RespBody> type,
            Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator
    );

    static <T> CompletableFuture<T> unwrap(CompletableFuture<HttpResponse<T>> response) {
        return response.thenApply(HttpResponse::body);
    }
}
