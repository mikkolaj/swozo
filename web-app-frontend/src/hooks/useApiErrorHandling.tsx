import { ApiError, ErrorType } from 'api/errors';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { TFunction } from 'i18next';
import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { triggerError } from 'services/features/error/errorSlice';
import { AppDispatch, useAppDispatch } from 'services/store';

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

export const buildMessagePopupErrorHandler = (dispatch: AppDispatch, message: string): ErrorHandler =>
    buildErrorHandler(() => {
        dispatch(triggerError({ message }));
    }, false);

const defaultErrorHandler: ErrorHandler = buildErrorHandler(() => <PageContainerWithError />);

const defaultConnectionErrorHandler = (dispatch: AppDispatch, t: TFunction): ErrorHandler =>
    buildMessagePopupErrorHandler(dispatch, t('error.connectionError'));

export const useApiErrorHandling = (
    handlerConfig: HandlerConfig,
    useFallbackHandler: boolean = true,
    fallbackErrorHandler: ErrorHandler = defaultErrorHandler,
    useFallbackConnectionErrorHandling: boolean = true
) => {
    const [apiErrors, setApiErrors] = useState<ApiError[]>([]);
    const [errorHandler, setErrorHandler] = useState<ErrorHandler | undefined>(NO_ERROR);
    const { t } = useTranslation();
    const dispatch = useAppDispatch();
    const [connectionErrorHandler] = useState(() => defaultConnectionErrorHandler(dispatch, t));

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
        } else if (apiErrors[0].errorType === ErrorType.CONNECTION_ERROR) {
            setErrorHandler(() => connectionErrorHandler);
        } else if (useFallbackHandler) {
            setErrorHandler(() => fallbackErrorHandler);
        }
    }, [
        handlerConfig,
        apiErrors,
        useFallbackHandler,
        fallbackErrorHandler,
        useFallbackConnectionErrorHandling,
        connectionErrorHandler,
    ]);

    useEffect(() => {
        if (apiErrors.length > 0 && !errorHandler?.shouldTerminateRendering) {
            consumeErrorAction();
        }
    }, [errorHandler, apiErrors.length, consumeErrorAction]);

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
