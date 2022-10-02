import { ApiError, ErrorType } from 'api/errors';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { useEffect, useState } from 'react';

const NO_ERROR = undefined;

type ErrorHandler = () => JSX.Element;
export type HandlerConfig = Partial<Record<ErrorType, ErrorHandler>>;

const defaultErrorHandler = () => <PageContainerWithError />;

export const useApiErrorHandling = (
    handlerConfig: HandlerConfig,
    useFallbackHandler: boolean = true,
    fallbackErrorHandler: ErrorHandler = defaultErrorHandler
) => {
    const [apiError, setApiError] = useState<ApiError | undefined>(NO_ERROR);
    const [errorHandler, setErrorHandler] = useState<ErrorHandler | undefined>(NO_ERROR);

    useEffect(() => {
        if (apiError === NO_ERROR) {
            setErrorHandler(NO_ERROR);
        } else if (handlerConfig[apiError.errorType]) {
            setErrorHandler(() => handlerConfig[apiError.errorType]);
        } else if (useFallbackHandler) {
            setErrorHandler(() => fallbackErrorHandler);
        }
    }, [handlerConfig, apiError, useFallbackHandler, fallbackErrorHandler]);

    return { apiError, isApiError: apiError !== NO_ERROR, setApiError, errorHandler };
};
