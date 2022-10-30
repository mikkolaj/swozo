package com.swozo.communication.http.decorators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RequestSenderHeaderDecorator extends RequestSenderDecorator {
    private final Function<HttpRequest.Builder, HttpRequest.Builder> headerDecorator;

    public RequestSenderHeaderDecorator(RequestSender wrappee, Function<HttpRequest.Builder, HttpRequest.Builder> headerDecorator) {
        super(wrappee);
        this.headerDecorator = headerDecorator;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type) {
        return this.sendGet(uri, type, Function.identity());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return super.sendGet(uri, type, headerDecorator.andThen(builderDecorator));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type) {
        return this.sendPost(uri, body, type, Function.identity());
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return super.sendPost(uri, body, type, headerDecorator.andThen(builderDecorator));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPut(URI uri, ReqBody body, TypeReference<RespBody> type) {
        return this.sendPut(uri, body, type, Function.identity());
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPut(URI uri, ReqBody body, TypeReference<RespBody> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return super.sendPut(uri, body, type, headerDecorator.andThen(builderDecorator));
    }
}
