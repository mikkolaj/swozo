export enum ErrorType {
    CONNECTION_ERROR = 'CONNECTION_ERROR',
    THIRD_PARTY_ERROR = 'THIRD_PARTY_ERROR',
    INTERNAL_SERVER_ERROR = 'INTERNAL_SERVER_ERROR',
    COURSE_NOT_FOUND = 'COURSE_NOT_FOUND',
    USER_NOT_FOUND = 'USER_NOT_FOUND',
    INVALID_COURSE_PASSWORD = 'INVALID_COURSE_PASSWORD',
    NOT_A_CREATOR = 'NOT_A_CREATOR',
    ALREADY_A_MEMBER = 'ALREADY_A_MEMBER',
    VALIDATION_FAILED = 'VALIDATION_FAILED',
    ACTIVITY_NOT_FOUND = 'ACTIVITY_NOT_FOUND',
    NOT_A_MEMBER = 'NOT_A_MEMBER',
    DUPLICATE_FILE = 'DUPLICATE_FILE',
    FILE_NOT_FOUND = 'FILE_NOT_FOUND',
    FAILED_TO_SEND_EMAIL = 'FAILED_TO_SEND_EMAIL',
    INVALID_CREDENTIALS = 'INVALID_CREDENTIALS',
    OPTION_NOT_ALLOWED = 'OPTION_NOT_ALLOWED',
    NOT_POSITIVE = 'NOT_POSITIVE',
}

export type ApiError = {
    errorType: ErrorType;
    message?: string;
    additionalData?: Record<string, unknown>;
};

export enum ValidationErrorType {
    MISSING = 'MISSING',
    NOT_UNIQUE = 'NOT_UNIQUE',
    START_TIME_AFTER_END = 'START_TIME_AFTER_END',
    TOO_SHORT_DURATION = 'TOO_SHORT_DURATION',
    TOO_LONG_DURATION = 'TOO_LONG_DURATION',
    TOO_SOON = 'TOO_SOON',
    TOO_SHORT_PERIOD_BETWEEN = 'TOO_SHORT_PERIOD_BETWEEN',
    NOT_IN_BOUNDS = 'NOT_IN_BOUNDS',
    INVALID_PASSWORD = 'INVALID_PASSWORD',
    INVALID_PASSWORD_TOKEN = 'INVALID_PASSWORD_TOKEN',
}

export type ValidationError = {
    fieldName: string;
    errorType: ValidationErrorType;
    args: Record<string, string>;
};
