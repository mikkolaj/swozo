import {
    FinishServiceModuleCreationRequest,
    ParameterDescriptionTypeEnum,
    ReserveServiceModuleRequest,
    ServiceModuleReservationDto,
} from 'api';
import { SlideValues2 } from 'common/SlideForm/util';
import _ from 'lodash';
import { AppDispatch } from 'services/store';
import { fieldActionHandlerFactory } from './components/dynamic/fieldActionHandlers';
import { extractValueForReservation } from './components/dynamic/utils';

export const MODULE_INFO_SLIDE = '0';
export const MODULE_SPECS_SLIDE = '1';

export type DynamicFormFields = Record<string, unknown>;
export type DynamicFormResolvedFieldActions = Record<string, string>;
export type DynamicFormValueRegistry = Record<
    string,
    {
        type: ParameterDescriptionTypeEnum;
        fieldValue: unknown;
        associatedValue?: unknown;
    }
>;

export type ModuleValues = {
    name: string;
    subject: string;
    description: string;
    service: string;
    serviceFile: string;
    instructions: string;
    isPublic: boolean;
};

export type ModuleSpecs = {
    environment: string;
    storage: number;
    cpu: string;
    ram: string;
};

export type FormValues = SlideValues2<ModuleValues, ModuleSpecs>;

export const initialModuleValues = (): ModuleValues => ({
    name: 'n',
    subject: 's',
    description: 'd',
    service: '',
    serviceFile: '',
    instructions: 'i',
    isPublic: true,
});

export const buildReserveServiceModuleRequest = (
    values: FormValues,
    dynamicFormValueRegistry: DynamicFormValueRegistry
): ReserveServiceModuleRequest => {
    const moduleInfo = values[MODULE_INFO_SLIDE];

    return {
        dynamicProperties: _.mapValues(dynamicFormValueRegistry, ({ type, associatedValue }) =>
            extractValueForReservation(type, associatedValue)
        ),
        name: moduleInfo.name,
        instructionHtml: moduleInfo.instructions,
        isPublic: moduleInfo.isPublic,
        scheduleTypeName: moduleInfo.service,
        subject: moduleInfo.subject,
        description: moduleInfo.description,
    };
};

export const handleAdditionalFieldActions = async (
    reserveServiceModuleRequest: ReserveServiceModuleRequest,
    reservationResp: ServiceModuleReservationDto,
    dynamicFormValueRegistry: DynamicFormValueRegistry,
    dispatch: AppDispatch
): Promise<DynamicFormResolvedFieldActions> => {
    return Object.fromEntries(
        await Promise.all(
            Object.entries(reservationResp.dynamicFieldAdditionalActions).map(async ([fieldName, action]) => {
                const fieldRegistry = dynamicFormValueRegistry[fieldName];
                const resultValue = await fieldActionHandlerFactory(fieldRegistry.type).handle(
                    fieldRegistry.associatedValue,
                    action,
                    dispatch,
                    reserveServiceModuleRequest,
                    reservationResp
                );
                return [fieldName, resultValue];
            })
        )
    );
};

export const buildFinishServiceModuleCreationRequest = (
    reserveServiceModuleRequest: ReserveServiceModuleRequest,
    reservationResp: ServiceModuleReservationDto,
    resolvedAdditionalFieldActions: DynamicFormResolvedFieldActions
): FinishServiceModuleCreationRequest => ({
    reservationId: reservationResp.reservationId,
    repeatedInitialValues: reserveServiceModuleRequest.dynamicProperties,
    echoFieldActions: _.mapValues(reservationResp.dynamicFieldAdditionalActions, (v) => JSON.stringify(v)),
    finalDynamicFieldValues: {
        ...reserveServiceModuleRequest.dynamicProperties,
        ...resolvedAdditionalFieldActions,
    },
});
