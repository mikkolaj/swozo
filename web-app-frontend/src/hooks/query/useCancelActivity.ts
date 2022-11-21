import { ActivityDetailsDto } from 'api';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { useTranslation } from 'react-i18next';
import { useMutation } from 'react-query';
import { toast } from 'react-toastify';

export const useCancelActivity = (
    cacheUpdater: (activity: ActivityDetailsDto) => void,
    pushApiError: (err: ApiError) => void
) => {
    const { t } = useTranslation();

    const cancelActivityMutation = useMutation(
        (activityId: number) => getApis().activitiesApi.cancelActivity({ activityId }),
        {
            onSuccess: (activityDetails) => {
                cacheUpdater(activityDetails);
                toast.success(t('toast.activityCancelled'));
            },
            onError: pushApiError,
        }
    );

    return { cancelActivityMutation };
};
