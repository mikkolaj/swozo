package com.swozo.communication.http.decorators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.exceptions.InvalidStatusCodeException;
import com.swozo.exceptions.ServiceUnavailableException;
import com.swozo.utils.ServiceType;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.swozo.utils.RetryHandler.withExponentialBackoff;

/**
 * Provides retry and error handling mechanism, in case of network error request is retried using exponential backoff,
 * if backoff timeouts ServiceUnavailableException is thrown, in case of HTTP status code errors (anything other than 2xx)
 * request is NOT retried and InvalidStatusCodeException is thrown
 */
public class RequestSenderEnhancerDecorator extends RequestSenderDecorator {
    private final ServiceType target;
    private final int backoffRetries;

    public RequestSenderEnhancerDecorator(RequestSender wrappee, ServiceType target, int backoffRetries) {
        super(wrappee);
        this.target = target;
        this.backoffRetries = backoffRetries;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type) {
        return enhance(() -> super.sendGet(uri, type));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type) {
        return enhance(() -> super.sendPost(uri, body, type));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPut(URI uri, ReqBody body, TypeReference<RespBody> type) {
        return enhance(() -> super.sendPut(uri, body, type));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return enhance(() -> super.sendGet(uri, type, builderDecorator));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return enhance(() -> super.sendPost(uri, body, type, builderDecorator));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPut(URI uri, ReqBody body, TypeReference<RespBody> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return enhance(() -> super.sendPut(uri, body, type, builderDecorator));
    }

    private <T> CompletableFuture<HttpResponse<T>> enhance(Supplier<CompletableFuture<HttpResponse<T>>> responseSupplier) {
        return withOkStatusAssertion(withExponentialBackoff(responseSupplier, backoffRetries));
    }

    private <T> CompletableFuture<HttpResponse<T>> withOkStatusAssertion(CompletableFuture<HttpResponse<T>> response) {
        return response.exceptionally(ex -> {
                    throw new ServiceUnavailableException(target, ex);
                })
                .thenApply(resp -> {
                    if (!HttpStatus.valueOf(resp.statusCode()).is2xxSuccessful())
                        throw new InvalidStatusCodeException(resp);
                    return resp;
                });
    }
}
