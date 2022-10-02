import { ApiError } from 'api/errors';
import { useEffect, useRef } from 'react';
import { QueryFunction, QueryKey, useQuery } from 'react-query';

export const useErrorHandledQuery = <T>(
    queryKey: QueryKey,
    queryFn: QueryFunction<T>,
    apiError: ApiError | undefined,
    setApiError: (ApiError?: ApiError) => void
) => {
    const { isError, error, ...rest } = useQuery(queryKey, queryFn);
    const prevApiError = useRef<ApiError>();
    // TODO: test if this behaves properly
    useEffect(() => {
        if (isError) {
            if (error !== apiError) {
                prevApiError.current = apiError;
                setApiError(error as unknown as ApiError);
            }
        } else if (prevApiError.current === apiError) {
            setApiError(undefined);
        }
    }, [error, apiError, isError, setApiError]);

    return { error, isError, ...rest };
};
