import { Box, Button, Divider, Grid, Paper, Tab, Tabs, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { InstructionView } from 'common/Styled/InstructionView';
import { RichTextViewer } from 'common/Styled/RichTextViewer';
import { stylesRowCenteredVertical, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useMeQuery } from 'hooks/query/useMeQuery';
import { HandlerConfig, useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useNoCourseOrNoActivityErrorHandlers } from 'hooks/useCommonErrorHandlers';
import { useCourseWithActivity } from 'hooks/useCourseActivity';
import { useRequiredParams } from 'hooks/useRequiredParams';
import _ from 'lodash';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { isSame } from 'utils/roles';
import { PageRoutes } from 'utils/routes';

export const ActivityInstructionsView = () => {
    const [activityId, courseId] = useRequiredParams(['activityId', 'courseId']);
    const { me } = useMeQuery();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [currentTab, setCurrentTab] = useState(0);
    const errorHandlers: HandlerConfig = {
        ...useNoCourseOrNoActivityErrorHandlers(courseId, activityId),
    };
    const { isApiError, errorHandler, isApiErrorSet, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling(errorHandlers);

    const { course, activity } = useCourseWithActivity(
        courseId,
        activityId,
        isApiErrorSet,
        pushApiError,
        removeApiError
    );
    console.log(activity);

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    if (!course || !activity) {
        return <PageContainerWithLoader />;
    }

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
                <Tab
                    label={t(
                        `activityInstructions.tabs.teacher.label.as.${
                            isSame(me, course.teacher) ? 'teacher' : 'default'
                        }`
                    )}
                />
                <Tab label={t('activityInstructions.tabs.modules.label')} />
            </Tabs>

            <Grid container sx={{ p: 2 }}>
                {currentTab === 0 && (
                    <Grid item xs={12}>
                        <InstructionView
                            wrapperSx={{ boxShadow: 3 }}
                            instruction={activity.instructionFromTeacher}
                        />
                    </Grid>
                )}

                {currentTab === 1 && (
                    <Grid container>
                        {activity.activityModules.map(({ serviceModule, id }) => (
                            <Grid key={id} item xs={12} sx={{ mb: 2 }}>
                                <Paper sx={{ width: '100%', p: 2, boxShadow: 3 }}>
                                    <Typography variant="h5" component="div" gutterBottom>
                                        {t('activityInstructions.tabs.modules.serviceModuleTitle', {
                                            serviceName: _.capitalize(serviceModule.serviceName),
                                            serviceModuleName: serviceModule.name,
                                        })}
                                    </Typography>
                                    <Divider sx={{ mb: 2 }} />
                                    {isSame(me, course.teacher) && (
                                        <Box>
                                            <Typography variant="h6" component="div">
                                                {t(
                                                    'activityInstructions.tabs.modules.teacherInstructionLabel'
                                                )}
                                            </Typography>
                                            <RichTextViewer
                                                wrapperSx={{ ml: 1 }}
                                                untrustedPossiblyDangerousHtml={
                                                    serviceModule.teacherInstruction
                                                        .untrustedPossiblyDangerousHtml
                                                }
                                            />
                                        </Box>
                                    )}
                                    <Typography variant="h6" component="div">
                                        {t('activityInstructions.tabs.modules.studentInstructionLabel')}
                                    </Typography>
                                    <RichTextViewer
                                        wrapperSx={{ ml: 1 }}
                                        untrustedPossiblyDangerousHtml={
                                            serviceModule.studentInstruction.untrustedPossiblyDangerousHtml
                                        }
                                    />
                                </Paper>
                            </Grid>
                        ))}
                    </Grid>
                )}
            </Grid>
        </PageContainer>
    );
};
