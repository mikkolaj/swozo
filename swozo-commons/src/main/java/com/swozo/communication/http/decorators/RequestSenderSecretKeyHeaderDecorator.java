package com.swozo.communication.http.decorators;

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
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, Class<T> clazz) {
        return this.sendGet(uri, clazz, Function.identity());
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, Class<T> clazz, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return super.sendGet(uri, clazz, builderDecorator.andThen(addSecretKey()));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, Class<RespBody> clazz) {
        return super.sendPost(uri, body, clazz);
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, Class<RespBody> clazz, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return super.sendPost(uri, body, clazz, builderDecorator.andThen(addSecretKey()));
    }

    private Function<HttpRequest.Builder, HttpRequest.Builder> addSecretKey() {
        return builder -> builder.header(SECRET_KEY_HEADER, secretKeySupplier.get());
    }
}
