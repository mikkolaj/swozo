import { Button, Container, Grid, Stack, Typography } from '@mui/material';
import { CourseDetailsDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
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
    tabRenderer: (course?: CourseDetailsDto) => JSX.Element;
};

const tabs: Record<Tab, TabConfig> = {
    activities: {
        tabRenderer: (course) => (
            <Stack spacing={2} sx={{ px: 2 }}>
                {course?.activities.map((activity) => (
                    <ActivityView key={activity.id} activity={activity} />
                ))}
            </Stack>
        ),
    },
    participants: {
        tabRenderer: () => <ParticipantsListView />,
    },
};

export const CourseView = () => {
    const [courseId] = useRequiredParams(['courseId']);
    const { t } = useTranslation();
    const [tab, setTab] = useState<TabConfig>(tabs['participants']);

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
                                <Button key={type} onClick={() => setTab(config)}>
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
