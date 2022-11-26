import {
    FinishServiceModuleCreationRequest,
    ReserveServiceModuleRequest,
    ServiceConfig,
    ServiceConfigIsolationModesEnum,
    ServiceModuleReservationDto,
    SharedServiceModuleMdaDto,
} from 'api';
import { ApiError } from 'api/errors';
import { fieldActionHandlerFactory } from 'common/DynamicFields/fieldActionHandlers';
import { mergeNestedKeyNames } from 'common/SlideForm/util';
import { FormikErrors } from 'formik';
import { TFunction } from 'i18next';
import _ from 'lodash';
import { argFormatter, FIELD_SEPARATOR } from 'pages/CreateCourse/util';
import { AppDispatch } from 'services/store';
import { prepareErrorForDisplay, prepareFormikValidationErrors } from 'utils/util';
import { extractValueForReservation } from '../../../common/DynamicFields/utils';
import {
    DynamicFormResolvedFieldActions,
    DynamicFormValueRegistry,
    FormValues,
    MdaValues,
    ModuleValues,
    MODULE_INFO_SLIDE,
    MODULE_SPECS_SLIDE,
} from './types';

const MODULE_INFO_SLIDE_NAME = 'moduleValues';
const MDA_INFO_SLIDE_NAME = 'mdaValues';
const DYNAMIC_FIELDS_NAME = '__dynamicFields__';

export const initialModuleValues = (): ModuleValues => ({
    name: '',
    subject: '',
    description: '',
    service: '',
    teacherInstruction: '',
    studentInstruction: '',
    isPublic: true,
});

export const initialMdaValues = (isIsolated: boolean): MdaValues => ({
    baseBandwidthMbps: 128,
    baseDiskGB: 1,
    baseRamGB: 1,
    baseVcpu: 1,
    isolationMode: isIsolated
        ? ServiceConfigIsolationModesEnum.Isolated
        : ServiceConfigIsolationModesEnum.Shared,
    sharedServiceModuleMdaDto: initialSharedModuleMdaValues(),
});

export const initialSharedModuleMdaValues = (): SharedServiceModuleMdaDto => ({
    usersPerAdditionalBandwidthGbps: 10,
    usersPerAdditionalCore: 10,
    usersPerAdditionalDiskGb: 10,
    usersPerAdditionalRamGb: 10,
});

export const toIsolationMode = (isIsolated: boolean): ServiceConfigIsolationModesEnum =>
    isIsolated ? ServiceConfigIsolationModesEnum.Isolated : ServiceConfigIsolationModesEnum.Shared;

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
        serviceName: moduleInfo.service,
        subject: moduleInfo.subject,
        description: moduleInfo.description,
        mdaData: {
            ..._.omit(values[MODULE_SPECS_SLIDE], 'isolationMode'),
            isIsolated: values[MODULE_SPECS_SLIDE].isolationMode === ServiceConfigIsolationModesEnum.Isolated,
        },
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
        service: data.serviceName,
        studentInstruction: data.studentInstruction.untrustedPossiblyDangerousHtml,
        teacherInstruction: data.teacherInstruction.untrustedPossiblyDangerousHtml,
        subject: data.subject,
    },
    [MODULE_SPECS_SLIDE]: {
        ...data.mdaData,
        isolationMode: toIsolationMode(data.mdaData.isIsolated),
        sharedServiceModuleMdaDto:
            data.mdaData.isIsolated || !data.mdaData.sharedServiceModuleMdaDto
                ? initialSharedModuleMdaValues()
                : data.mdaData.sharedServiceModuleMdaDto,
    },
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

export const formatErrors = (t: TFunction, error: ApiError): FormikErrors<FormValues> => {
    const modulePrefix = MODULE_INFO_SLIDE_NAME + FIELD_SEPARATOR;
    const mdaPrefix = MDA_INFO_SLIDE_NAME + FIELD_SEPARATOR;

    return prepareFormikValidationErrors(
        error,
        (key) =>
            key.startsWith(modulePrefix)
                ? key.replace(modulePrefix, MODULE_INFO_SLIDE + FIELD_SEPARATOR)
                : key.replace(mdaPrefix, MODULE_SPECS_SLIDE + FIELD_SEPARATOR),
        (error) => prepareErrorForDisplay(t, 'createModule', error, argFormatter)
    );
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const flattenDynamicErrors = (errors: any) => {
    const dynamicFieldsPrefix = MODULE_INFO_SLIDE + FIELD_SEPARATOR + DYNAMIC_FIELDS_NAME + FIELD_SEPARATOR;
    return _.mapKeys(mergeNestedKeyNames(errors), (_, key: string) =>
        key.startsWith(dynamicFieldsPrefix) ? key.substring(dynamicFieldsPrefix.length) : key
    );
};
