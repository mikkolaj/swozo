import {
    FinishServiceModuleCreationRequest,
    ReserveServiceModuleRequest,
    ServiceConfig,
    ServiceModuleReservationDto,
} from 'api';
import { fieldActionHandlerFactory } from 'common/DynamicFields/fieldActionHandlers';
import _ from 'lodash';
import { AppDispatch } from 'services/store';
import { extractValueForReservation } from '../../../common/DynamicFields/utils';
import {
    DynamicFormResolvedFieldActions,
    DynamicFormValueRegistry,
    FormValues,
    ModuleSpecs,
    ModuleValues,
    MODULE_INFO_SLIDE,
    MODULE_SPECS_SLIDE,
} from './types';

export const initialModuleValues = (): ModuleValues => ({
    name: 'nowy moduł',
    subject: 'informatyka',
    description: 'opis',
    service: '',
    teacherInstruction: '',
    studentInstruction: '',
    isPublic: true,
});

export const initialModuleSpecsValues = (): ModuleSpecs => ({
    environment: 'isolated',
    storage: 1,
    cpu: 'big',
    ram: 'big',
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
        teacherInstruction: { untrustedPossiblyDangerousHtml: moduleInfo.teacherInstruction },
        studentInstruction: { untrustedPossiblyDangerousHtml: moduleInfo.studentInstruction },
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

export const mapToInitialValues = (data: ReserveServiceModuleRequest): FormValues => ({
    [MODULE_INFO_SLIDE]: {
        name: data.name,
        description: data.description,
        isPublic: data.isPublic,
        service: data.scheduleTypeName,
        studentInstruction: data.studentInstruction.untrustedPossiblyDangerousHtml,
        teacherInstruction: data.teacherInstruction.untrustedPossiblyDangerousHtml,
        subject: data.subject,
    },
    [MODULE_SPECS_SLIDE]: initialModuleSpecsValues(),
});

export const preprocessSupportedServices = (
    supportedServices: ServiceConfig[] | undefined,
    editMode: boolean,
    values: FormValues
) => {
    if (!supportedServices) return [];
    return editMode
        ? supportedServices.filter((service) => service.serviceName === values[MODULE_INFO_SLIDE].service)
        : supportedServices;
};
