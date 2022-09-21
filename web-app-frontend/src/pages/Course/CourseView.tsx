import { Button, Container, Grid, Stack, Typography } from '@mui/material';
import { CourseDetailsDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useRequiredParams } from 'hooks/useRequiredParams';
import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import { PageRoutes } from 'utils/routes';
import { ActivityView } from './components/Activity/ActivityView';
import { ParticipantsListView } from './components/Options/ParticipantsList/ParticipantsListView';

export const CourseContext = React.createContext<CourseDetailsDto | undefined>(undefined);

type Tab = 'activities' | 'participants';
type TabConfig = {
    type: Tab;
    tabRenderer: (course: CourseDetailsDto) => JSX.Element;
};

const tabs: Record<Tab, TabConfig> = {
    activities: {
        type: 'activities',
        tabRenderer: (course) => (
            <Stack spacing={2} sx={{ px: 2 }}>
                {course.activities.map((activity) => (
                    <ActivityView key={activity.id} activity={activity} />
                ))}
            </Stack>
        ),
    },
    participants: {
        type: 'participants',
        tabRenderer: (course) => <ParticipantsListView course={course} />,
    },
};

export const CourseView = () => {
    const [courseId] = useRequiredParams(['courseId']);
    const { t } = useTranslation();
    const [tab, setTab] = useState<TabConfig>(tabs['activities']);

    const { data: course, isError } = useQuery(['courses', courseId], () =>
        getApis().courseApi.getCourse({ id: +courseId })
    );

    if (isError) {
        return (
            <PageContainerWithError
                navButtonMessage={t('course.error.noCourse')}
                navigateTo={PageRoutes.MY_COURSES}
            />
        );
    }

    if (!course) {
        return <PageContainerWithLoader />;
    }

    return (
        <CourseContext.Provider value={course}>
            <PageContainer
                sx={{ p: 0 }}
                header={
                    <>
                        <Grid item xs={6}>
                            <Typography variant="h4" component="div">
                                {course?.name}
                            </Typography>
                        </Grid>
                        <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                            {Object.entries(tabs).map(([type, config]) => (
                                <Button
                                    key={type}
                                    onClick={() => setTab(config)}
                                    variant={tab.type === type ? 'contained' : 'outlined'}
                                    sx={{ mr: 0.5 }}
                                >
                                    {t(`course.options.${type}.button`)}
                                </Button>
                            ))}
                        </Grid>
                    </>
                }
            >
                <Container>{tab.tabRenderer(course)}</Container>
            </PageContainer>
        </CourseContext.Provider>
    );
};
