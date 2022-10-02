import { ApiError, ValidationError, ValidationErrorType } from 'api/errors';
import dayjs, { Dayjs } from 'dayjs';
import { TFunction } from 'i18next';
import _ from 'lodash';

export const DATE_FORMAT = 'DD.MM.YYYY';

export const TIME_FORMAT = 'HH:mm';

export const DATE_TIME_FORMAT = `${TIME_FORMAT} ${DATE_FORMAT}`;

export const formatDate = (date: Date): string => {
    return dayjs(date).format(DATE_FORMAT);
};

export const formatTime = (date: Date): string => {
    return dayjs(date).format(TIME_FORMAT);
};

export const formatDateTime = (date: Date): string => {
    return dayjs(date).format(DATE_TIME_FORMAT);
};

export const withDate = (time: Dayjs, date: Dayjs): Dayjs =>
    time.set('year', date.get('year')).set('month', date.get('month')).set('day', date.get('day'));

export const formatName = (firstName?: string, lastName?: string) =>
    _.capitalize(`${firstName ?? ''} ${lastName ?? ''}`).trim();

export const loadFromLocalStorage = <T>(key: string): T | undefined => {
    const data = window.localStorage.getItem(key);
    return data ? JSON.parse(data) : undefined;
};

export const persistWithLocalStorage = <T>(key: string, data: T) =>
    window.localStorage.setItem(key, JSON.stringify(data));

export const clearLocalStorage = (key: string) => window.localStorage.removeItem(key);

export const resolveI18nValidationError = (i18nPrefix: string, error: ValidationErrorType): string =>
    `${i18nPrefix}.validationErrors.${error}`;

export const prepareErrorForDisplay = (
    t: TFunction,
    i18nPrefix: string,
    error: ValidationError,
    argFormatter: (key: string, val: string) => string = (k, v) => v
): string => {
    return t(
        resolveI18nValidationError(i18nPrefix, error.errorType),
        _.mapValues(error.args, (value, key) => argFormatter(key, value))
    );
};

export const prepareFormikValidationErrors = (
    error: ApiError,
    keyMapper: (key: string) => string,
    valueMapper: (val: ValidationError) => string
) => {
    return Object.entries(error.additionalData ?? {})
        .map(([key, error]) => [keyMapper(key), valueMapper(error as ValidationError)])
        .reduce((acc, [fieldPath, error]) => _.set(acc, fieldPath, error), {});
};
