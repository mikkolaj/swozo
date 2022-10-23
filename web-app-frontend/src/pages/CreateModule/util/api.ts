import { ReserveServiceModuleRequest, ServiceModuleDetailsDto, ServiceModuleSummaryDto } from 'api';
import { getApis } from 'api/initialize-apis';
import _ from 'lodash';
import { QueryClient } from 'react-query';
import { AppDispatch } from 'services/store';
import { buildFinishServiceModuleCreationRequest, handleAdditionalFieldActions } from './mapping';
import { DynamicFormValueRegistry } from './types';

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
    const updatedSerivceModule = await getApis().serviceModuleApi.updateCommonData({
        reserveServiceModuleRequest,
        serviceModuleId: +serviceModuleId,
    });

    if (_.isEmpty(reserveServiceModuleRequest.dynamicProperties)) {
        return updatedSerivceModule;
    }

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
    queryClient: QueryClient,
    editMode: boolean
) => {
    const updateOldModules = (allModules: ServiceModuleSummaryDto[] = []) => {
        if (editMode) {
            return [resp, ...allModules.filter((module) => module.id !== resp.id)];
        }

        return [resp, ...allModules];
    };

    queryClient.setQueryData(['modules', 'summary', 'public'], updateOldModules);
    queryClient.setQueryData(['modules', 'summary', 'me'], updateOldModules);
    queryClient.setQueryData(['modules', `${resp.id}`, 'details'], resp);

    if (editMode) {
        queryClient.removeQueries(['modules', `${resp.id}`, 'edit']);
    }
};
