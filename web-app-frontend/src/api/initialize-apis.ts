import { AuthControllerApi, Configuration, ExampleControllerApi } from 'api';
import { getAccessToken } from 'services/features/auth/auth';

type Apis = {
    authApi: AuthControllerApi;
    exampleApi: ExampleControllerApi;
};

export const initializeApis = (): Apis => {
    // TODO config for dev/prod from env
    const configuration = new Configuration({
        accessToken: getAccessToken,
    });

    return {
        authApi: new AuthControllerApi(configuration),
        exampleApi: new ExampleControllerApi(configuration),
    };
};

const apis: Apis = initializeApis();

export const getApis = (): Apis => {
    return apis;
};
