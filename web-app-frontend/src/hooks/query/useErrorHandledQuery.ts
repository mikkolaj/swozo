import { ApiError } from 'api/errors';
import { useEffect, useRef } from 'react';
import { QueryFunction, QueryKey, useQuery } from 'react-query';

export const useErrorHandledQuery = <T>(
    queryKey: QueryKey,
    queryFn: QueryFunction<T>,
    pushApiError: (apiError: ApiError) => void,
    removeApiError: (apiError: ApiError) => void
) => {
    const { isError, error, ...rest } = useQuery(queryKey, queryFn);
    const memoedError = useRef<ApiError>();

    useEffect(() => {
        if (isError) {
            memoedError.current = error as ApiError;
            pushApiError(error as ApiError);
        } else if (memoedError.current !== undefined) {
            removeApiError(memoedError.current);
            memoedError.current = undefined;
        }
    }, [error, isError, pushApiError, removeApiError]);

    return { error, isError, ...rest };
};
