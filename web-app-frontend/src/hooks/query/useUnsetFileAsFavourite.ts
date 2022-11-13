import { FileDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useDispatch } from 'react-redux';
import { toast } from 'react-toastify';
import { triggerError } from 'services/features/error/errorSlice';

export const useUnsetFileAsFavourite = () => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();
    const dispatch = useDispatch();
    const unsetFileAsFavouriteMutation = useMutation(
        (file: FileDto) => getApis().userApi.unsetFileAsFavourite({ remoteFileId: file.id }),
        {
            onSuccess: (me) => {
                queryClient.setQueryData(['me'], me);
                toast.success(t('toast.removedFromFavourite'));
            },
            onError: () => {
                dispatch(triggerError({ message: t('error.tryAgain') }));
            },
        }
    );

    return { unsetFileAsFavouriteMutation };
};
