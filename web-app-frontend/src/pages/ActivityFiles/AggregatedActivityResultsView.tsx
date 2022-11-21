import DownloadIcon from '@mui/icons-material/Download';
import { Box, Divider, IconButton, Typography } from '@mui/material';
import { ActivityDetailsDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { stylesRowCenteredVertical } from 'common/styles';
import { useDownload } from 'hooks/query/useDownload';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { formatName } from 'utils/util';
import { StudentAcitivtyFilesGroupedByModule, toUserActivityFilesGroupedByModuleId } from './utils';

type Props = {
    activity: ActivityDetailsDto;
};

export const AggregatedActivityResultsView = ({ activity }: Props) => {
    const { t } = useTranslation();
    const { pushApiError, removeApiError, isApiErrorSet } = useApiErrorHandling({});
    const [filesByModuleId, setFilesByModuleId] = useState<StudentAcitivtyFilesGroupedByModule>();

    const { data: files } = useErrorHandledQuery(
        ['activities', `${activity.id}`, 'files', 'results', 'students'],
        () => getApis().activitiesApi.getActivityResultFilesForAllStudents({ activityId: activity.id }),
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

    useEffect(() => {
        if (files) {
            setFilesByModuleId(toUserActivityFilesGroupedByModuleId(files, activity));
        }
    }, [files, activity]);

    if (!filesByModuleId) return <></>;

    return (
        <Box>
            {filesByModuleId.map(([activityModule, studentFiles]) => (
                <Box key={activityModule.id} sx={{ mt: 4 }}>
                    <Box sx={{ p: 2 }}>
                        <Divider />
                    </Box>
                    <Typography variant="h5" gutterBottom sx={{ my: 2 }}>
                        {t('activityFiles.tabs.users.module', {
                            name: activityModule.serviceModule.name,
                            serviceName: activityModule.serviceModule.serviceName,
                        })}
                    </Typography>
                    <StackedList
                        /* eslint-disable react/jsx-key */
                        header={
                            <StackedListHeader
                                proportions={[4, 8]}
                                items={['studentName', 'studentEmail'].map((label) => (
                                    <Typography variant="body1" color="GrayText">
                                        {t(`activityFiles.tabs.users.allStudents.headers.${label}`)}
                                    </Typography>
                                ))}
                            />
                        }
                        content={
                            <StackedListContent
                                items={studentFiles}
                                proportions={[4, 7, 1]}
                                itemKeyExtractor={({ file }) => file.id}
                                itemRenderer={({ file, student }) => [
                                    <Typography>{formatName(student.name, student.surname)}</Typography>,
                                    <Typography>{student.email}</Typography>,
                                    <IconButton
                                        sx={{ ml: 'auto' }}
                                        color="primary"
                                        onClick={() => download(file)}
                                    >
                                        <DownloadIcon />
                                    </IconButton>,
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
                                            {t('activityFiles.tabs.users.allStudents.empty')}
                                        </Typography>
                                    </Box>
                                }
                            />
                        }
                    />
                </Box>
            ))}
        </Box>
    );
};
