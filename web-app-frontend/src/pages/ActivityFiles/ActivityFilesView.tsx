import DownloadIcon from '@mui/icons-material/Download';
import { Box, Button, Container, Divider, Grid, IconButton, Tab, Tabs, Typography } from '@mui/material';
import { ActivityDetailsDto } from 'api';
import { ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { FileInputButton } from 'common/Input/FileInputButton';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import {
    stylesRowCenteredHorizontal,
    stylesRowCenteredVertical,
    stylesRowWithItemsAtTheEnd,
} from 'common/styles';
import { useDownload } from 'hooks/query/useDownload';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useMeQuery } from 'hooks/query/useMeQuery';
import { useUpload } from 'hooks/query/useUpload';
import { HandlerConfig, useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { buildErrorPageHandler, useFileErrorHandlers } from 'hooks/useCommonErrorHandlers';
import { useRequiredParams } from 'hooks/useRequiredParams';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { isSame } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { formatDateTime } from 'utils/util';

export const ActivityFilesView = () => {
    const [activityId, courseId] = useRequiredParams(['activityId', 'courseId']);
    const { t } = useTranslation();
    const { me } = useMeQuery();
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const [currentTab, setCurrentTab] = useState(0);
    const [activity, setActivity] = useState<ActivityDetailsDto>();
    const [errorHandlers] = useState<HandlerConfig>({
        [ErrorType.COURSE_NOT_FOUND]: buildErrorPageHandler(
            t('activityInstructions.error.noCourse'),
            PageRoutes.MY_COURSES
        ),
        [ErrorType.ACTIVITY_NOT_FOUND]: buildErrorPageHandler(
            t('activityInstructions.error.noActivity'),
            PageRoutes.Course(courseId)
        ),
        ...useFileErrorHandlers(),
    });
    const { isApiError, errorHandler, isApiErrorSet, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling(errorHandlers);

    const { data: course } = useErrorHandledQuery(
        ['courses', courseId],
        () => getApis().courseApi.getCourse({ id: +courseId }),
        pushApiError,
        removeApiError
    );

    useEffect(() => {
        const activity = course?.activities.find((activity) => activity.id === +activityId);
        const activityNotFoundError = { errorType: ErrorType.ACTIVITY_NOT_FOUND };

        if (activity) {
            setActivity(activity);
            if (isApiErrorSet(activityNotFoundError)) {
                removeApiError(activityNotFoundError);
            }
        } else if (course) {
            pushApiError(activityNotFoundError);
        }
    }, [course, activityId, pushApiError, removeApiError, isApiErrorSet]);

    const { download } = useDownload({
        fetcher: (file) =>
            getApis().activitiesApi.getPublicActivityFileDownloadRequest({
                activityId: +activityId,
                fileId: file.id,
            }),
        onError: pushApiError,
        deps: [activityId],
    });

    const { upload, isUploading } = useUpload<ActivityDetailsDto>({
        uploadContext: PageRoutes.ACTIVITY_FILES,
        preparator: (initFileUploadRequest) =>
            getApis().activitiesApi.preparePublicActivityFileUpload({
                activityId: +activityId,
                initFileUploadRequest,
            }),
        acker: (uploadAccessDto) =>
            getApis().activitiesApi.ackPublicActivityFileUpload({
                activityId: +activityId,
                uploadAccessDto,
            }),
        onSuccess: (activity) => {
            queryClient.setQueryData(['courses', courseId], {
                ...course,
                activities: [
                    ...(course?.activities.filter((activity) => activity.id !== +activityId) ?? []),
                    activity,
                ],
            });
            toast.success(t('toast.fileUploaded'));
        },
        onError: pushApiError,
        deps: [activityId, course, courseId, queryClient],
    });

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    if (!course || !activity) {
        return <PageContainerWithLoader />;
    }

    return (
        <PageContainer>
            <Grid
                container
                sx={{
                    ...stylesRowCenteredVertical,
                    justifyContent: 'space-between',
                }}
            >
                <Grid item xs={8}>
                    <Typography sx={{ p: 2 }} variant="h4" component="div">
                        {t('activityFiles.header', {
                            courseName: course.name,
                            activityName: activity.name,
                        })}
                    </Typography>
                </Grid>
                <Grid item xs={4} sx={{ ...stylesRowWithItemsAtTheEnd, pr: 1 }}>
                    <Button onClick={() => navigate(PageRoutes.Course(course.id))}>
                        {t('activityFiles.goBackBtn')}
                    </Button>
                </Grid>
            </Grid>
            <Divider />
            <Tabs value={currentTab} onChange={(_, tab) => setCurrentTab(tab)} centered variant="fullWidth">
                <Tab label={t('activityFiles.tabs.public.label')} />
                <Tab label={t('activityFiles.tabs.users.label')} />
            </Tabs>

            <Grid container sx={{ p: 2 }}>
                {currentTab === 0 && (
                    <Container sx={{ mt: 4 }}>
                        <StackedList
                            content={
                                <StackedListContent
                                    items={activity.publicFiles}
                                    proportions={[9, 2, 1]}
                                    itemKeyExtractor={(file) => file.id}
                                    itemRenderer={(file) => [
                                        /* eslint-disable react/jsx-key */
                                        <Typography>{file.name}</Typography>,
                                        <Typography>{formatDateTime(file.createdAt)}</Typography>,
                                        <IconButton color="primary" onClick={() => download(file)}>
                                            <DownloadIcon />
                                        </IconButton>,
                                        /* eslint-enable react/jsx-key */
                                    ]}
                                />
                            }
                        />
                        {isSame(me, course.teacher) && (
                            <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mt: 4 }}>
                                <FileInputButton
                                    disabled={isUploading}
                                    sx={{ minWidth: 200 }}
                                    text={t('activityFiles.tabs.public.uploadBtn')}
                                    onFileSelected={upload}
                                />
                            </Box>
                        )}
                    </Container>
                )}

                {currentTab === 1 && <Grid container>TODO</Grid>}
            </Grid>
        </PageContainer>
    );
};
