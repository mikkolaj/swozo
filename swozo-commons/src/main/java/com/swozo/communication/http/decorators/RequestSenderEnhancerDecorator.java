package com.swozo.communication.http.decorators;

import com.swozo.communication.http.RequestSender;
import com.swozo.exceptions.InvalidStatusCodeException;
import com.swozo.exceptions.ServiceUnavailableException;
import com.swozo.utils.RetryHandler;
import com.swozo.utils.ServiceType;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

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
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, Class<T> clazz) {
        return withOkStatusAssertion(super.sendGet(uri, clazz));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, Class<RespBody> clazz) {
        return enhance(() -> super.sendPost(uri, body, clazz));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, Class<T> clazz, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return enhance(() -> super.sendGet(uri, clazz, builderDecorator));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, Class<RespBody> clazz, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return enhance(() -> super.sendPost(uri, body, clazz, builderDecorator));
    }

    private <T> CompletableFuture<HttpResponse<T>> enhance(Supplier<CompletableFuture<HttpResponse<T>>> responseSupplier) {
        return withOkStatusAssertion(withExponentialBackoff(responseSupplier));
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

    private <T> CompletableFuture<HttpResponse<T>> withExponentialBackoff(
            Supplier<CompletableFuture<HttpResponse<T>>> responseSupplier
    ) {
        try {
            return RetryHandler.retryExponentially(() -> {
                var result = responseSupplier.get();
                result.get();
                return result;
            }, backoffRetries);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
