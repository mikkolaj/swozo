import { InitFileUploadRequest, ParameterDescriptionTypeEnum } from 'api';
import { i18n } from 'i18next';

export type FieldUtils = {
    setFieldValue: (value: unknown, type: ParameterDescriptionTypeEnum) => void;
    setAssociatedValue?: (value: unknown) => void;
};

export const getInitialValue = (type: ParameterDescriptionTypeEnum): unknown => {
    switch (type) {
        case ParameterDescriptionTypeEnum.File:
        case ParameterDescriptionTypeEnum.Text:
        default:
            return '';
    }
};

export const extractValueForReservation = (type: ParameterDescriptionTypeEnum, value: unknown): string => {
    if (type === ParameterDescriptionTypeEnum.File) {
        const file = value as File;
        const req: InitFileUploadRequest = {
            filename: file.name,
            sizeBytes: file.size,
        };
        return JSON.stringify(req);
    }

    if (type === ParameterDescriptionTypeEnum.Text) {
        return value as string;
    }

    console.error(`Unsupported dynamic property type: ${type}`);
    return '';
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
