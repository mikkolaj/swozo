import { Box, Button, Divider, Grid, Paper, Tab, Tabs, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import { mockCourse } from 'utils/mocks';
import { PageRoutes } from 'utils/routes';

export const ActivityInstructionsView = () => {
    const { activityId, courseId } = useParams();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [currentTab, setCurrentTab] = useState(0);
    const [course] = useState(mockCourse);
    const [activity] = useState(activityId && mockCourse.activities.find(({ id }) => id === +activityId));

    if (!activity) {
        // TODO handle it properly when backend is up
        return (
            <div>
                No activity in course: {courseId} with id: {activityId}
            </div>
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
                    <Box>
                        {activity.instructions.map(({ header, body }, idx) => (
                            <Paper
                                key={idx}
                                sx={{ mb: idx < activity.instructions.length - 1 ? 2 : 0, p: 2 }}
                            >
                                <Typography variant="h5">{header}</Typography>
                                <Divider sx={{ mb: 2 }} />
                                <Typography variant="body1">{body}</Typography>
                            </Paper>
                        ))}
                    </Box>
                )}

                {currentTab === 1 && <Grid container>TODO</Grid>}
            </Grid>
        </PageContainer>
    );
};
