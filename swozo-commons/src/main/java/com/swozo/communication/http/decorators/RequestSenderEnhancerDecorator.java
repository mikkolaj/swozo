package com.swozo.communication.http.decorators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.exceptions.InvalidStatusCodeException;
import com.swozo.exceptions.PropagatingException;
import com.swozo.exceptions.ServiceUnavailableException;
import com.swozo.utils.RetryManager;
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
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type) {
        return enhance(() -> super.sendGet(uri, type));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type) {
        return enhance(() -> super.sendPost(uri, body, type));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendGet(URI uri, TypeReference<T> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return enhance(() -> super.sendGet(uri, type, builderDecorator));
    }

    @Override
    public <ReqBody, RespBody> CompletableFuture<HttpResponse<RespBody>> sendPost(URI uri, ReqBody body, TypeReference<RespBody> type, Function<HttpRequest.Builder, HttpRequest.Builder> builderDecorator) {
        return enhance(() -> super.sendPost(uri, body, type, builderDecorator));
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

    // TODO move it to RetryHandler and try make it more reusable
    // https://github.com/mikkolaj/swozo/pull/24#discussion_r999386507
    private <T> CompletableFuture<HttpResponse<T>> withExponentialBackoff(
            Supplier<CompletableFuture<HttpResponse<T>>> requestSender
    ) {
        var retryMgr = new RetryManager(backoffRetries);
        return requestSender.get().exceptionallyComposeAsync(err -> handleRetries(requestSender, retryMgr, err));
    }

    private <T> CompletableFuture<HttpResponse<T>> handleRetries(
            Supplier<CompletableFuture<HttpResponse<T>>> requestSender,
            RetryManager retryMgr,
            Throwable previousErr
    ) {
        if (retryMgr.canContinue()) {
            try {
                Thread.sleep(retryMgr.nextBackoffMillis());
                return requestSender.get().exceptionallyComposeAsync(err -> handleRetries(requestSender, retryMgr, err));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        throw new RuntimeException(previousErr);
    }
}
