import { QueryFunction, QueryKey, useQuery } from 'react-query';

export const useQueryWithDefaults = <T>(queryKey: QueryKey, queryFn: QueryFunction<T>, defaultVal: T) => {
    const { data, isLoading, isError, ...rest } = useQuery(queryKey, queryFn);
    const resData = data === undefined || isLoading || isError ? defaultVal : data;
    return { data: resData, isError, isLoading, ...rest };
};
