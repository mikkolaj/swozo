import { Box, Button, Container, Grid, Stack, Typography } from '@mui/material';
import { AuthDetailsDtoRolesEnum, CourseDetailsDto } from 'api';
import { ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesColumnCenteredHorizontal, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { HandlerConfig, useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { buildErrorPageHandler } from 'hooks/useCommonErrorHandlers';
import { useRequiredParams } from 'hooks/useRequiredParams';
import _ from 'lodash';
import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { TEACHER, WithRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { ActivityView } from './components/Activity/ActivityView';
import { Editor } from './components/Options/ParticipantsList/Editor';
import { ParticipantsListView } from './components/Options/ParticipantsList/ParticipantsListView';

export const CourseContext = React.createContext<CourseDetailsDto | undefined>(undefined);

type Tab = 'activities' | 'participants' | 'editor';
type TabConfig = {
    type: Tab;
    roles?: AuthDetailsDtoRolesEnum[];
    tabRenderer: (course: CourseDetailsDto) => JSX.Element;
};

const tabs: Record<Tab, TabConfig> = {
    activities: {
        type: 'activities',
        tabRenderer: (course) => (
            <Stack spacing={2} sx={{ px: 2 }}>
                {_.sortBy(course.activities, (activity) => activity.startTime).map((activity) => (
                    <ActivityView key={activity.id} activity={activity} />
                ))}
            </Stack>
        ),
    },
    participants: {
        type: 'participants',
        tabRenderer: (course) => <ParticipantsListView course={course} />,
    },
    editor: {
        type: 'editor',
        roles: [TEACHER],
        tabRenderer: (course) => (
            <Editor course={course} initialTab={course.activities.length > 0 ? 'course' : 'addActivity'} />
        ),
    },
};

export const CourseView = () => {
    const [courseId] = useRequiredParams(['courseId']);
    const { t } = useTranslation();
    const [tab, setTab] = useState<TabConfig>(tabs['activities']);
    const [errorHandlers] = useState<HandlerConfig>({
        [ErrorType.COURSE_NOT_FOUND]: buildErrorPageHandler(
            t('course.error.noCourse'),
            PageRoutes.MY_COURSES
        ),
    });
    const { isApiError, errorHandler, consumeErrorAction, isApiErrorSet, pushApiError, removeApiError } =
        useApiErrorHandling(errorHandlers);

    const { data: course } = useErrorHandledQuery(
        ['courses', courseId],
        () => getApis().courseApi.getCourse({ id: +courseId }),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    return (
        <CourseContext.Provider value={course}>
            <PageContainer
                sx={{ p: 0 }}
                header={
                    <>
                        <Grid item xs={7}>
                            <PageHeaderText text={course?.name} />
                        </Grid>
                        <Grid item xs={5} sx={stylesRowWithItemsAtTheEnd}>
                            {Object.entries(tabs).map(([type, config]) => (
                                <WithRole roles={config.roles ?? []} key={type}>
                                    <Button
                                        onClick={() => setTab(config)}
                                        variant={tab.type === type ? 'contained' : 'outlined'}
                                        sx={{ mr: 0.5, height: '50px' }}
                                    >
                                        {t(`course.options.${type}.button`)}
                                    </Button>
                                </WithRole>
                            ))}
                        </Grid>
                    </>
                }
            >
                <Container>
                    {course &&
                        (tab.type !== 'activities' || course.activities.length > 0 ? (
                            tab.tabRenderer(course)
                        ) : (
                            <Box sx={{ ...stylesColumnCenteredHorizontal, justifyContent: 'center', mt: 8 }}>
                                <Typography
                                    sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }}
                                    variant="h4"
                                >
                                    {t('course.options.activities.empty')}
                                </Typography>
                                <Button
                                    variant="contained"
                                    sx={{ mt: 4, px: 4, py: 2 }}
                                    onClick={() => setTab(tabs['editor'])}
                                >
                                    <Typography variant="h5">
                                        {t('course.options.activities.emptyButton')}
                                    </Typography>
                                </Button>
                            </Box>
                        ))}
                </Container>
            </PageContainer>
        </CourseContext.Provider>
    );
};
