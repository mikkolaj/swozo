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
import { ApiError, ErrorType } from './errors';

type Apis = {
    authApi: AuthControllerApi;
    userApi: UserControllerApi;
    courseApi: CourseControllerApi;
    serviceModuleApi: ServiceModuleControllerApi;
};

const is4xxFailure = (response: Response) => response.status >= 400 && response.status < 500;

class ErrorPreprocessorMiddleware implements Middleware {
    async post({ response, url }: ResponseContext): Promise<Response | void> {
        if (!response.ok) {
            console.error(`Request for: ${url} failed with status: ${response.status}`);
            if (is4xxFailure(response)) {
                let errorBody;
                try {
                    // TODO check Content-Type if correct body might not be json
                    errorBody = await response.json();
                } catch (err) {
                    throw new Error('Failed to parse error body');
                }

                throw errorBody as ApiError;
            } else {
                throw {
                    message: 'Unexpected server error',
                    errorType: ErrorType.INTERNAL_SERVER_ERROR,
                };
            }
        }

        return response;
    }
}

export const initializeApis = (): Apis => {
    const configuration = new Configuration({
        accessToken: getAccessToken,
        middleware: [new ErrorPreprocessorMiddleware()],
        basePath: process.env.REACT_APP_BASE_PATH,
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
