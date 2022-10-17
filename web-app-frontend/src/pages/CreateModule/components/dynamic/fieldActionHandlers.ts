import { ParameterDescriptionTypeEnum } from 'api';

export interface FieldActionHandler {
    handle(): Promise<string>;
    getType(): ParameterDescriptionTypeEnum;
}

export const fieldActionHandlerFactory = (type: ParameterDescriptionTypeEnum): FieldActionHandler => {
    switch (type) {
        case ParameterDescriptionTypeEnum.File:
            return new FileActionHandler();
        case ParameterDescriptionTypeEnum.Text:
            return new TextActionHandler();
        default:
            throw new Error(`No action handler for ${type}`);
    }
};

export class FileActionHandler implements FieldActionHandler {
    getType(): ParameterDescriptionTypeEnum {
        return ParameterDescriptionTypeEnum.File;
    }

    handle() {
        return Promise.resolve('');
    }
}

export class TextActionHandler implements FieldActionHandler {
    getType(): ParameterDescriptionTypeEnum {
        return ParameterDescriptionTypeEnum.Text;
    }

    handle(): Promise<string> {
        return Promise.resolve('');
    }
}
