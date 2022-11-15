import DownloadIcon from '@mui/icons-material/Download';
import { Box, IconButton, Typography } from '@mui/material';

import { ActivityDetailsDto, AuthDetailsDtoRolesEnum } from 'api';
import { getApis } from 'api/initialize-apis';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { FavouriteToggle } from 'common/Styled/FavouriteToggle';
import { stylesRow, stylesRowCenteredVertical } from 'common/styles';
import { useDownload } from 'hooks/query/useDownload';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useMeQuery } from 'hooks/query/useMeQuery';
import { useSetFileAsFavourite } from 'hooks/query/useSetFileAsFavouriteMutation';
import { useUnsetFileAsFavourite } from 'hooks/query/useUnsetFileAsFavourite';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useTranslation } from 'react-i18next';
import { toUserActivityFiles } from './utils';

type Props = {
    activity: ActivityDetailsDto;
    showFor: AuthDetailsDtoRolesEnum;
};

export const UserActivityResultsView = ({ activity, showFor }: Props) => {
    const { t } = useTranslation();
    const { pushApiError, removeApiError, isApiErrorSet } = useApiErrorHandling({});
    const { me } = useMeQuery();

    const { data: files } = useErrorHandledQuery(
        ['activities', `${activity.id}`, 'files', 'results'],
        () => getApis().activitiesApi.getActivityResultFilesForUser({ activityId: activity.id }),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    const { download } = useDownload({
        fetcher: (file) =>
            getApis().activitiesApi.getActivityResultFileDownloadRequest({
                activityId: activity.id,
                fileId: file.id,
            }),
        onError: pushApiError,
        deps: [activity],
    });

    const { setFileAsFavouriteMutation } = useSetFileAsFavourite(activity.id);
    const { unsetFileAsFavouriteMutation } = useUnsetFileAsFavourite();

    return (
        <Box>
            {showFor === AuthDetailsDtoRolesEnum.Teacher && (
                <Typography variant="h5" gutterBottom sx={{ my: 2 }}>
                    {t('activityFiles.tabs.users.my.teacherLabel')}
                </Typography>
            )}
            <StackedList
                /* eslint-disable react/jsx-key */
                header={
                    <StackedListHeader
                        proportions={[4, 8]}
                        items={['name', 'serviceModule'].map((label) => (
                            <Typography variant="body1" color="GrayText">
                                {t(`activityFiles.tabs.users.my.headers.${label}`)}
                            </Typography>
                        ))}
                    />
                }
                content={
                    <StackedListContent
                        items={toUserActivityFiles(activity, files ?? { activityModuleIdToUserFiles: {} })}
                        proportions={[4, 7, 1]}
                        itemKeyExtractor={({ file }) => file.id}
                        itemRenderer={({ activityModule, file }) => [
                            <Typography>{file.name}</Typography>,
                            <Typography>{activityModule.serviceModule.name}</Typography>,
                            <Box sx={{ ...stylesRow, ml: 'auto' }}>
                                <IconButton color="primary" onClick={() => download(file)}>
                                    <DownloadIcon />
                                </IconButton>
                                {showFor === AuthDetailsDtoRolesEnum.Student && (
                                    <FavouriteToggle
                                        isFavourite={
                                            !!me?.favouriteFiles.find(
                                                ({ file: favFile }) => favFile.id === file.id
                                            )
                                        }
                                        onSetFavourite={() => setFileAsFavouriteMutation.mutate(file)}
                                        onUnsetFavourite={() => unsetFileAsFavouriteMutation.mutate(file)}
                                    />
                                )}
                            </Box>,
                            /* eslint-enable react/jsx-key */
                        ]}
                        emptyItemsComponent={
                            <Box
                                sx={{
                                    ...stylesRowCenteredVertical,
                                    pt: 1,
                                    textAlign: 'center',
                                }}
                            >
                                <Typography variant="h6">
                                    {t(
                                        `activityFiles.tabs.users.my.empty${
                                            showFor === AuthDetailsDtoRolesEnum.Teacher
                                                ? 'Teacher'
                                                : 'Student'
                                        }`
                                    )}
                                </Typography>
                            </Box>
                        }
                    />
                }
            />
        </Box>
    );
};
