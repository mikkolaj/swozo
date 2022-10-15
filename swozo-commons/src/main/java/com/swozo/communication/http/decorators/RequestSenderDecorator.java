package com.swozo.communication.http.decorators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RequiredArgsConstructor
public class RequestSenderDecorator implements RequestSender {
    protected final RequestSender wrappee;

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type) {
        return wrappee.sendGet(uri, type);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T>type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return wrappee.sendGet(uri, type, builderDecorator);
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type) {
        return wrappee.sendPost(uri, body, type);
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return wrappee.sendPost(uri, body, type, builderDecorator);
    }
}
