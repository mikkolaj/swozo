package com.swozo.communication.http.decorators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class RequestSenderSecretKeyHeaderDecorator extends RequestSenderDecorator {
    private final Supplier<String> secretKeySupplier;
    private final String SECRET_KEY_HEADER = HttpHeaders.AUTHORIZATION;

    public RequestSenderSecretKeyHeaderDecorator(RequestSender wrappee, Supplier<String> secretKeySupplier) {
        super(wrappee);
        this.secretKeySupplier = secretKeySupplier;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type) {
        return this.sendGet(uri, type, Function.identity());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return super.sendGet(uri, type, builderDecorator.andThen(addSecretKey()));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type) {
        return super.sendPost(uri, body, type);
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return super.sendPost(uri, body, type, builderDecorator.andThen(addSecretKey()));
    }

    private Function<HttpRequest.Builder, HttpRequest.Builder> addSecretKey() {
        return builder -> builder.header(SECRET_KEY_HEADER, secretKeySupplier.get());
    }
}
