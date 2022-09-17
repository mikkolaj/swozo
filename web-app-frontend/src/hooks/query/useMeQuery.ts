import { getApis } from 'api/initialize-apis';
import { useQuery } from 'react-query';

export const useMeQuery = () => {
    const { data: me, ...rest } = useQuery('me', () => getApis().userApi.getUserInfo());
    return { me, ...rest };
};
