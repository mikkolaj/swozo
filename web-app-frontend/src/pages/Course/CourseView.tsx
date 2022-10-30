import { Button, Container, Grid, Stack, Typography } from '@mui/material';
import { CourseDetailsDto } from 'api';
import { ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
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
        tabRenderer: (course) => (
            <WithRole roles={[TEACHER]}>
                <Editor course={course} />
            </WithRole>
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
    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling(errorHandlers);

    const { data: course } = useErrorHandledQuery(
        ['courses', courseId],
        () => getApis().courseApi.getCourse({ id: +courseId }),
        pushApiError,
        removeApiError
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
                            <Typography variant="h4" component="div">
                                {course?.name}
                            </Typography>
                        </Grid>
                        <Grid item xs={5} sx={stylesRowWithItemsAtTheEnd}>
                            {Object.entries(tabs).map(([type, config]) => (
                                <Button
                                    key={type}
                                    onClick={() => setTab(config)}
                                    variant={tab.type === type ? 'contained' : 'outlined'}
                                    sx={{ mr: 0.5, height: '50px' }}
                                >
                                    {t(`course.options.${type}.button`)}
                                </Button>
                            ))}
                        </Grid>
                    </>
                }
            >
                <Container>{course && tab.tabRenderer(course)}</Container>
            </PageContainer>
        </CourseContext.Provider>
    );
};
