import {
    ParameterDescriptionTypeEnum,
    ReserveServiceModuleRequest,
    ServiceModuleReservationDto,
    StorageAccessRequest,
} from 'api';
import { upload } from 'services/features/files/fileSlice';
import { AppDispatch } from 'services/store';

export interface FieldActionHandler {
    handle(
        fieldValue: unknown,
        actionValue: unknown,
        dispatch: AppDispatch,
        reservationReq: ReserveServiceModuleRequest,
        reservationResp: ServiceModuleReservationDto
    ): Promise<string>;

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

    handle(fieldValue: File, actionValue: StorageAccessRequest, dispatch: AppDispatch): Promise<string> {
        return new Promise((res, rej) => {
            dispatch(
                upload({
                    file: fieldValue,
                    preparator: () => Promise.resolve(actionValue),
                    acker: () => Promise.resolve(undefined),
                    onSuccess: () => res(actionValue.filePath),
                    onError: (err) => rej(err),
                    uploadContext: 'service-modules',
                })
            );
        });
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
