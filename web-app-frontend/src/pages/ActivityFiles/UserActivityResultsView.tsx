import DownloadIcon from '@mui/icons-material/Download';
import { Box, Typography } from '@mui/material';
import { ActivityDetailsDto, AuthDetailsDtoRolesEnum } from 'api';
import { getApis } from 'api/initialize-apis';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { stylesRowCenteredVertical } from 'common/styles';
import { useDownload } from 'hooks/query/useDownload';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
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
                            <ButtonWithIconAndText
                                sx={{ ml: 'auto' }}
                                color="primary"
                                onClick={() => download(file)}
                                textI18n={'activityFiles.tabs.users.download'}
                                Icon={DownloadIcon}
                            />,
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
