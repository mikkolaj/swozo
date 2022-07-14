import { Box, Container, Divider, Grid, Stack, Typography } from '@mui/material';
import { CourseDetailsDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { useRequiredParams } from 'hooks/useRequiredParams';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import { PageRoutes } from 'utils/routes';
import { ActivityView } from './components/Activity/ActivityView';

export const CourseContext = React.createContext<CourseDetailsDto | undefined>(undefined);

export const CourseView = () => {
    const [courseId] = useRequiredParams(['courseId']);
    const { t } = useTranslation();

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
            <PageContainer sx={{ p: 0 }}>
                <Grid container sx={{ p: 2 }}>
                    <Grid item xs={6}>
                        <Typography variant="h4" component="div">
                            {course?.name}
                        </Typography>
                    </Grid>
                </Grid>
                <Divider />
                <Container sx={{ mt: 4 }}>
                    <Stack spacing={2} sx={{ px: 2 }}>
                        {course?.activities.map((activity) => (
                            <ActivityView key={activity.id} activity={activity} />
                        ))}
                    </Stack>
                    <Box sx={{ height: 1000 }} />
                </Container>
            </PageContainer>
        </CourseContext.Provider>
    );
};
