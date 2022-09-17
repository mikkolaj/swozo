import { Button, Divider, Grid, Paper, Tab, Tabs, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { stylesRowCenteredVertical, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useRequiredParams } from 'hooks/useRequiredParams';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import 'react-quill/dist/quill.snow.css';
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

    const quillHtml =
        '<p><strong>sdfsd</strong></p><p><strong>&lt;</strong><a href="script" rel="noopener noreferrer" target="_blank"><strong>script</strong></a><strong>&gt;&lt;/script&gt;</strong></p><ol><li><strong>tak</strong></li><li><strong>nie</strong></li><li><strong>byc moze</strong></li></ol><h1>Halo</h1><p><br></p><p><br></p>';

    return (
        <PageContainer sx={{ p: 0, position: 'relative' }}>
            <Grid
                container
                sx={{
                    ...stylesRowCenteredVertical,
                    justifyContent: 'space-between',
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
                <Grid item xs={4} sx={{ ...stylesRowWithItemsAtTheEnd, pr: 1 }}>
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
                                <Typography variant="body1">
                                    <span
                                        className="ql-editor"
                                        dangerouslySetInnerHTML={{ __html: quillHtml }}
                                    />
                                </Typography>
                            </Paper>
                        ))}
                    </Grid>
                )}

                {currentTab === 1 && <Grid container>TODO</Grid>}
            </Grid>
        </PageContainer>
    );
};
