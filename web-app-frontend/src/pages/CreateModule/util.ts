import {
    FinishServiceModuleCreationRequest,
    ParameterDescriptionTypeEnum,
    ReserveServiceModuleRequest,
    ServiceConfig,
    ServiceModuleDetailsDto,
    ServiceModuleReservationDto,
    ServiceModuleSummaryDto,
} from 'api';
import { getApis } from 'api/initialize-apis';
import { SlideValues2 } from 'common/SlideForm/util';
import _ from 'lodash';
import { QueryClient } from 'react-query';
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
    teacherInstruction: string;
    studentInstruction: string;
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

export const createServiceModule = async (
    reserveServiceModuleRequest: ReserveServiceModuleRequest,
    dynamicFormValueRegistry: DynamicFormValueRegistry,
    dispatch: AppDispatch
) => {
    const reservationResp = await getApis().serviceModuleApi.initServiceModuleCreation({
        reserveServiceModuleRequest,
    });

    const resolvedAdditionalFieldActions = await handleAdditionalFieldActions(
        reserveServiceModuleRequest,
        reservationResp,
        dynamicFormValueRegistry,
        dispatch
    );

    return getApis().serviceModuleApi.finishServiceModuleCreation({
        finishServiceModuleCreationRequest: buildFinishServiceModuleCreationRequest(
            reserveServiceModuleRequest,
            reservationResp,
            resolvedAdditionalFieldActions
        ),
    });
};

export const updateServiceModule = async (
    reserveServiceModuleRequest: ReserveServiceModuleRequest,
    serviceModuleId: string,
    dynamicFormValueRegistry: DynamicFormValueRegistry,
    dispatch: AppDispatch
) => {
    await getApis().serviceModuleApi.updateCommonData({
        reserveServiceModuleRequest,
        serviceModuleId: +serviceModuleId,
    });

    const additionalActions = await getApis().serviceModuleApi.initServiceConfigUpdate({
        reserveServiceModuleRequest,
        serviceModuleId: +serviceModuleId,
    });

    const resolvedAdditionalFieldActions = await handleAdditionalFieldActions(
        reserveServiceModuleRequest,
        { dynamicFieldAdditionalActions: additionalActions, reservationId: +serviceModuleId },
        dynamicFormValueRegistry,
        dispatch
    );

    return getApis().serviceModuleApi.finishServiceConfigUpdate({
        serviceModuleId: +serviceModuleId,
        finishServiceModuleCreationRequest: {
            reservationId: +serviceModuleId,
            echoFieldActions: _.mapValues(additionalActions, (v) => JSON.stringify(v)),
            repeatedInitialValues: reserveServiceModuleRequest.dynamicProperties,
            finalDynamicFieldValues: resolvedAdditionalFieldActions,
        },
    });
};

export const updateCacheAfterServiceModuleChange = (
    resp: ServiceModuleDetailsDto,
    queryClient: QueryClient
) => {
    queryClient.setQueryData(
        ['modules', 'summary', 'public'],
        (allModules: ServiceModuleSummaryDto[] = []) => [resp, ...allModules]
    );
    queryClient.setQueryData(['modules', 'summary', 'me'], (allModules: ServiceModuleSummaryDto[] = []) => [
        resp,
        ...allModules,
    ]);
    queryClient.setQueryData(['modules', `${resp.id}`, 'details'], resp);
};

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
