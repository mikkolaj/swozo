import {
    AuthControllerApi,
    Configuration,
    CourseControllerApi,
    Middleware,
    ResponseContext,
    ServiceModuleControllerApi,
    UserControllerApi,
} from 'api';
import { getAccessToken } from 'services/features/auth/auth';

type Apis = {
    authApi: AuthControllerApi;
    userApi: UserControllerApi;
    courseApi: CourseControllerApi;
    serviceModuleApi: ServiceModuleControllerApi;
};

class ErrorPreprocessorMiddleware implements Middleware {
    async post({ response, url }: ResponseContext): Promise<Response | void> {
        if (!response.ok) {
            console.error(`Request for: ${url} failed with status: ${response.status}`);
            let errorBody;
            try {
                // TODO check Content-Type if correct body might not be json
                errorBody = await response.json();
            } catch (err) {
                throw new Error('Failed to parse error body');
            }

            throw errorBody;
        }

        return response;
    }
}

export const initializeApis = (): Apis => {
    // TODO config for dev/prod from env
    const configuration = new Configuration({
        accessToken: getAccessToken,
        middleware: [new ErrorPreprocessorMiddleware()],
    });

    return {
        authApi: new AuthControllerApi(configuration),
        userApi: new UserControllerApi(configuration),
        courseApi: new CourseControllerApi(configuration),
        serviceModuleApi: new ServiceModuleControllerApi(configuration),
    };
};

const apis: Apis = initializeApis();

export const getApis = (): Apis => {
    return apis;
};
