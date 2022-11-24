import { FileDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useDispatch } from 'react-redux';
import { toast } from 'react-toastify';
import { triggerError } from 'services/features/error/errorSlice';

export const useSetFileAsFavourite = (activityId: number) => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();
    const dispatch = useDispatch();

    const setFileAsFavouriteMutation = useMutation(
        (file: FileDto) => getApis().userApi.setFileAsFavourite({ remoteFileId: file.id, activityId }),
        {
            onSuccess: (me) => {
                queryClient.setQueryData(['me'], me);
                toast.success(t('toast.addedToFavourite'));
            },
            onError: () => {
                dispatch(triggerError({ message: t('error.tryAgain') }));
            },
        }
    );

    return { setFileAsFavouriteMutation };
};
