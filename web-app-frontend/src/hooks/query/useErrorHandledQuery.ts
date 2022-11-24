import { ApiError } from 'api/errors';
import { useEffect, useRef, useState } from 'react';
import { QueryFunction, QueryKey, useQuery } from 'react-query';

export const useErrorHandledQuery = <T>(
    queryKey: QueryKey,
    queryFn: QueryFunction<T>,
    pushApiError: (apiError: ApiError) => void,
    removeApiError: (apiError: ApiError) => void,
    isApiErrorSet: (error: ApiError) => boolean,
    enabled: boolean = true
) => {
    const [err, setErr] = useState<unknown>(null);
    const { isError, error, ...rest } = useQuery(queryKey, queryFn, { enabled });
    const memoedError = useRef<ApiError>();

    useEffect(() => {
        if (isError && memoedError.current !== error) setErr(error);
    }, [isError, error]);

    useEffect(() => {
        if (err !== null) {
            memoedError.current = err as ApiError;
            if (!isApiErrorSet(err as ApiError)) {
                pushApiError(err as ApiError);
            }
            setErr(null);
        } else if (!isError && memoedError.current !== undefined) {
            removeApiError(memoedError.current);
            memoedError.current = undefined;
        }
    }, [isError, err, pushApiError, removeApiError, isApiErrorSet]);

    return { error, isError, ...rest };
};
