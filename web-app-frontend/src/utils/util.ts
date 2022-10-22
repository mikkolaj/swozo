import { ApiError, ValidationError, ValidationErrorType } from 'api/errors';
import dayjs, { Dayjs } from 'dayjs';
import { i18n, TFunction } from 'i18next';
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

export const getTranslated = (i18n: i18n, translations?: Record<string, string>) => {
    if (!translations) {
        return '';
    }

    const preferredFallback = 'en';
    const locale = i18n.language;

    return (
        translations[locale.toUpperCase()] ??
        translations[preferredFallback.toUpperCase()] ??
        Object.values(translations)[0] ??
        ''
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

export const sleep = (sleepTimeMillis: number) => new Promise((res) => setTimeout(res, sleepTimeMillis));

export async function withExponentialBackoff<T>(
    fetcher: () => Promise<T>,
    maxRetries: number,
    maxTimeMillis: number,
    maxSleepTimeMillis: number = 1000
): Promise<T> {
    const start = new Date();
    const initialSleepMilis = 100;
    let lastErr = undefined;

    for (let i = 0; i <= maxRetries; i++) {
        try {
            return await fetcher();
        } catch (err) {
            if (new Date().getTime() - start.getTime() <= maxTimeMillis) {
                await sleep(Math.min(maxSleepTimeMillis, initialSleepMilis * Math.pow(2, i)));
                lastErr = err;
            } else {
                throw err;
            }
        }
    }

    throw lastErr;
}
