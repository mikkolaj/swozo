import { ApiError, ErrorType } from 'api/errors';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { useCallback, useEffect, useState } from 'react';

const NO_ERROR = undefined;

export type ErrorHandler = {
    shouldTerminateRendering: boolean;
    handler: () => JSX.Element | void;
};
export type HandlerConfig = Partial<Record<ErrorType, ErrorHandler>>;

export const buildErrorHandler = (
    handler: () => JSX.Element | void,
    shouldTerminateRendering: boolean = true
): ErrorHandler => ({
    handler,
    shouldTerminateRendering,
});

const defaultErrorHandler: ErrorHandler = buildErrorHandler(() => <PageContainerWithError />);

export const useApiErrorHandling = (
    handlerConfig: HandlerConfig,
    useFallbackHandler: boolean = true,
    fallbackErrorHandler: ErrorHandler = defaultErrorHandler
) => {
    const [apiErrors, setApiErrors] = useState<ApiError[]>([]);
    const [errorHandler, setErrorHandler] = useState<ErrorHandler | undefined>(NO_ERROR);

    const consumeErrorAction = useCallback(() => {
        if (!errorHandler) return;

        if (apiErrors.length > 0 && !errorHandler.shouldTerminateRendering) {
            errorHandler?.handler();
            setApiErrors(apiErrors.slice(1));
        } else if (errorHandler.shouldTerminateRendering) {
            return errorHandler.handler();
        }
    }, [errorHandler, apiErrors]);

    const pushApiError = useCallback(
        (error: ApiError) => {
            if (!apiErrors.find((err) => err.errorType === error.errorType)) {
                setApiErrors([error, ...apiErrors]);
            }
        },
        [apiErrors]
    );

    const removeApiError = useCallback(
        (error: ApiError) => setApiErrors(apiErrors.filter((err) => err.errorType === error.errorType)),
        [apiErrors]
    );

    const isApiErrorSet = useCallback(
        (error: ApiError) => apiErrors.find((err) => err.errorType === error.errorType) !== undefined,
        [apiErrors]
    );

    useEffect(() => {
        if (apiErrors.length === 0) {
            setErrorHandler(NO_ERROR);
        } else if (handlerConfig[apiErrors[0].errorType]) {
            setErrorHandler(() => handlerConfig[apiErrors[0].errorType]);
        } else if (useFallbackHandler) {
            setErrorHandler(() => fallbackErrorHandler);
        }
    }, [handlerConfig, apiErrors, useFallbackHandler, fallbackErrorHandler]);

    return {
        apiErrors,
        isApiError: apiErrors.length > 0,
        errorHandler,
        isApiErrorSet,
        consumeErrorAction,
        pushApiError,
        removeApiError,
    };
};
