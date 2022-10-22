import { ServiceModuleSummaryDto } from 'api';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { PageRoutes } from 'utils/routes';

export const useDeleteServiceModule = (pushApiError: (error: ApiError) => void) => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const queryClient = useQueryClient();

    const mutation = useMutation(
        (serviceModuleId: string) =>
            getApis().serviceModuleApi.deleteServiceModule({ serviceModuleId: +serviceModuleId }),
        {
            onSuccess: (resp) => {
                toast.success(t('toast.serviceModuleDeleted'));

                queryClient.setQueryData(
                    ['modules', 'summary', 'public'],
                    (allModules: ServiceModuleSummaryDto[] = []) =>
                        allModules.filter((module) => module.id !== resp.id)
                );
                queryClient.setQueryData(
                    ['modules', 'summary', 'me'],
                    (allModules: ServiceModuleSummaryDto[] = []) =>
                        allModules.filter((module) => module.id !== resp.id)
                );
                queryClient.removeQueries(['modules', `${resp.id}`]);
                queryClient.removeQueries(['modules', `${resp.id}`, 'details']);

                navigate(PageRoutes.MY_MODULES);
            },
            onError: pushApiError,
        }
    );

    return {
        serviceModuleDeleteMutation: mutation,
    };
};
