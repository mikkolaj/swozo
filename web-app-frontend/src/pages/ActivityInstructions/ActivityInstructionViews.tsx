import { Button, Divider, Grid, Paper, Tab, Tabs, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { useRequiredParams } from 'hooks/useRequiredParams';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';

export const ActivityInstructionsView = () => {
    const [activityId, courseId] = useRequiredParams(['activityId', 'courseId']);
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [currentTab, setCurrentTab] = useState(0);
    const { data: course, isError } = useQuery(['courses', courseId], () =>
        getApis().courseApi.getCourse({ id: +courseId })
    );

    if (isError) {
        return (
            <PageContainerWithError
                navButtonMessage={t('activityInstructions.error.noCourse')}
                navigateTo={PageRoutes.MY_COURSES}
            />
        );
    }

    // TODO
    if (!course) {
        return <>loading</>;
    }

    const activity = course.activities.find((activity) => activity.id === +activityId);

    if (!activity) {
        return (
            <PageContainerWithError
                navButtonMessage={t('activityInstructions.error.noActivity')}
                navigateTo={PageRoutes.Course(courseId)}
            />
        );
    }

    return (
        <PageContainer sx={{ p: 0, position: 'relative' }}>
            <Grid
                container
                sx={{
                    display: 'flex',
                    flexDirection: 'row',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <Grid item xs={8}>
                    <Typography sx={{ p: 2 }} variant="h4" component="div">
                        {t('activityInstructions.header', {
                            courseName: course.name,
                            activityName: activity.name,
                        })}
                    </Typography>
                </Grid>
                <Grid
                    item
                    xs={4}
                    sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'flex-end', pr: 1 }}
                >
                    <Button onClick={() => navigate(PageRoutes.Course(course.id))}>
                        {t('activityInstructions.goBackBtn')}
                    </Button>
                </Grid>
            </Grid>
            <Divider />
            <Tabs value={currentTab} onChange={(_, tab) => setCurrentTab(tab)} centered variant="fullWidth">
                {/* /TODO diffrerent name if viewed as teacher? */}
                <Tab label={t('activityInstructions.tabs.teacher.label')} />
                <Tab label={t('activityInstructions.tabs.modules.label')} />
            </Tabs>

            <Grid container sx={{ p: 2 }}>
                {currentTab === 0 && (
                    <Grid item xs={12}>
                        {activity.instructionsFromTeacher.map(({ header, body }, idx) => (
                            <Paper
                                key={idx}
                                sx={{
                                    width: '100%',
                                    mb: idx < activity.instructionsFromTeacher.length - 1 ? 2 : 0,
                                    p: 2,
                                }}
                            >
                                <Typography variant="h5">{header}</Typography>
                                <Divider sx={{ mb: 2 }} />
                                <Typography variant="body1">{body}</Typography>
                            </Paper>
                        ))}
                    </Grid>
                )}

                {currentTab === 1 && <Grid container>TODO</Grid>}
            </Grid>
        </PageContainer>
    );
};
