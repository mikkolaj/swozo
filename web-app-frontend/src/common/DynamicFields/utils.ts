import { InitFileUploadRequest, ParameterDescriptionTypeEnum } from 'api';

export type InputFieldUtils = {
    setFieldValue: (value: unknown, type: ParameterDescriptionTypeEnum) => void;
    setAssociatedValue?: (value: unknown) => void;
};

export type DisplayFieldUtils = {
    onInteractionError: (error: unknown) => void;
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
