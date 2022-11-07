import {
    ActivityControllerApi,
    AuthControllerApi,
    Configuration,
    CourseControllerApi,
    FileControllerApi,
    Middleware,
    PolicyControllerApi,
    ResponseContext,
    SandboxControllerApi,
    ServiceModuleControllerApi,
    UserControllerApi,
    VmControllerApi,
} from 'api';
import { appConfig } from 'index';
import { getAccessToken } from 'services/features/auth/auth';
import { withExponentialBackoff } from 'utils/util';
import { ApiError, ErrorType } from './errors';

type Apis = {
    authApi: AuthControllerApi;
    userApi: UserControllerApi;
    courseApi: CourseControllerApi;
    activitiesApi: ActivityControllerApi;
    serviceModuleApi: ServiceModuleControllerApi;
    fileApi: FileControllerApi;
    sandboxApi: SandboxControllerApi;
    vmApi: VmControllerApi;
    policyApi: PolicyControllerApi;
};

const is4xxFailure = (response: Response) => response.status >= 400 && response.status < 500;

class ErrorPreprocessorMiddleware implements Middleware {
    async post({ response, url }: ResponseContext): Promise<Response | void> {
        if (!response.ok) {
            console.error(`Request for: ${url} failed with status: ${response.status}`);
            const fallbackError: ApiError = {
                message: 'Unexpected server error',
                errorType: ErrorType.INTERNAL_SERVER_ERROR,
            };

            if (is4xxFailure(response) || response.status === 503) {
                let errorBody;
                try {
                    // TODO check Content-Type if correct body might not be json
                    errorBody = await response.json();
                } catch (err) {
                    console.error('Failed to parse error body');
                    throw fallbackError;
                }

                throw errorBody as ApiError;
            } else {
                throw fallbackError;
            }
        }

        return response;
    }
}

export const initializeApis = (): Apis => {
    const configuration = new Configuration({
        accessToken: getAccessToken,
        middleware: [new ErrorPreprocessorMiddleware()],
        fetchApi: (input, init) => {
            return withExponentialBackoff(
                () => fetch(input, init),
                appConfig.fetchConfig.retries,
                appConfig.fetchConfig.maxTimeMillis
            );
        },
        basePath: process.env.REACT_APP_BASE_PATH,
    });

    return {
        authApi: new AuthControllerApi(configuration),
        userApi: new UserControllerApi(configuration),
        courseApi: new CourseControllerApi(configuration),
        activitiesApi: new ActivityControllerApi(configuration),
        serviceModuleApi: new ServiceModuleControllerApi(configuration),
        fileApi: new FileControllerApi(configuration),
        sandboxApi: new SandboxControllerApi(configuration),
        vmApi: new VmControllerApi(configuration),
        policyApi: new PolicyControllerApi(configuration),
    };
};

const apis: Apis = initializeApis();

export const getApis = (): Apis => {
    return apis;
};
