package com.swozo.communication.http.decorators;

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
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, Class<T> clazz) {
        return wrappee.sendGet(uri, clazz);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, Class<T> clazz, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return wrappee.sendGet(uri, clazz, builderDecorator);
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, Class<RespBody> clazz) {
        return wrappee.sendPost(uri, body, clazz);
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, Class<RespBody> clazz, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return wrappee.sendPost(uri, body, clazz, builderDecorator);
    }
}
