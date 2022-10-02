export enum ErrorType {
    INTERNAL_SERVER_ERROR = 'INTERNAL_SERVER_ERROR',
    COURSE_NOT_FOUND = 'COURSE_NOT_FOUND',
    USER_NOT_FOUND = 'USER_NOT_FOUND',
    INVALID_COURSE_PASSWORD = 'INVALID_COURSE_PASSWORD',
    NOT_A_CREATOR = 'NOT_A_CREATOR',
    ALREADY_A_MEMBER = 'ALREADY_A_MEMBER',
}

export type ApiError = {
    errorType: ErrorType;
    message?: string;
    additionalData?: Record<string, unknown>;
};
