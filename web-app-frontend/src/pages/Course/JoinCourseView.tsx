import SchoolIcon from '@mui/icons-material/School';
import { Box, Button, Grid, TextField, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import {
    stylesRow,
    stylesRowCenteredHorizontal,
    stylesRowCenteredVertical,
    stylesRowWithSpaceBetweenItems,
} from 'common/styles';
import { useRequiredParams } from 'hooks/useRequiredParams';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQuery, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { PageRoutes } from 'utils/routes';

export const JoinCourseView = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [joinUUID] = useRequiredParams(['joinUUID']);
    const { data: userCourses } = useQuery(['courses'], () => getApis().courseApi.getUserCourses());

    const [password, setPassword] = useState('');
    const queryClient = useQueryClient();
    const { data: course, isError } = useQuery(['courses', 'public', joinUUID], () =>
        getApis().courseApi.getPublicCourseData({ uuid: joinUUID })
    );

    const joinCourseMutation = useMutation(
        () =>
            getApis().courseApi.joinCourse({
                joinCourseRequest: { joinUUID, password: course?.isPasswordProtected ? password : undefined },
            }),
        {
            onSuccess: (course) => {
                queryClient.setQueryData(['courses', `${course.id}`], course);
                toast.success(t('course.join.joinedMessage'));
                navigate(PageRoutes.Course(course.id));
            },
        }
    );

    if (isError) {
        return <PageContainerWithError errorMessage={t('course.join.error.invalidJoinUrl')} />;
    }

    if (userCourses && userCourses.find((course) => course.joinUUID === joinUUID)) {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        const courseDetails = userCourses.find((course) => course.joinUUID === joinUUID)!;

        return (
            <PageContainerWithError
                navigateTo={PageRoutes.Course(courseDetails.id)}
                navButtonMessage={t('course.join.error.goToCourse')}
                errorMessage={t('course.join.error.alreadyParticipated', {
                    name: courseDetails.name,
                })}
            />
        );
    }

    if (!course) {
        return <PageContainerWithLoader />;
    }

    return (
        <PageContainer>
            <AbsolutelyCentered>
                <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mb: 1, mt: -25 }}>
                    <SchoolIcon sx={{ width: 150, height: 150 }} />
                </Box>
                <Grid container sx={{ margin: 'auto', maxWidth: '80%' }}>
                    <Grid item xs={12} sx={{ ...stylesRow, justifyContent: 'center' }}>
                        <Typography variant="h2">{t('course.join.joinCourseInfo')}</Typography>
                    </Grid>
                    <Grid item xs={12} sx={{ ...stylesRow, justifyContent: 'center' }}>
                        <Typography variant="h3">{course.name}</Typography>
                    </Grid>
                    {course.isPasswordProtected && (
                        <Grid item xs={12} sx={{ ...stylesRowCenteredVertical, justifyContent: 'center' }}>
                            <Typography variant="h4" sx={{ fontSize: 30, mt: 4, mr: 2 }}>
                                {t('course.join.password')}
                            </Typography>
                            <TextField
                                variant="standard"
                                type="password"
                                autoFocus
                                onKeyDown={(e) => {
                                    if (e.nativeEvent.key === 'Enter' && password) {
                                        joinCourseMutation.mutate();
                                    }
                                }}
                                sx={{ width: 180, mt: 3 }}
                                inputProps={{
                                    style: {
                                        fontSize: 30,
                                        height: 20,
                                    },
                                }}
                                value={password}
                                onChange={(v) => setPassword(v.target.value)}
                            />
                        </Grid>
                    )}
                    <Grid item xs={12} sx={{ mt: 6 }}>
                        <Box sx={{ ...stylesRowWithSpaceBetweenItems, margin: 'auto', maxWidth: '60%' }}>
                            <Button
                                sx={{ width: 250 }}
                                variant="outlined"
                                onClick={() => navigate(PageRoutes.HOME)}
                            >
                                {t('course.join.cancel')}
                            </Button>
                            <Button
                                sx={{ width: 250 }}
                                variant="contained"
                                disabled={
                                    joinCourseMutation.isLoading || (course.isPasswordProtected && !password)
                                }
                                onClick={() => joinCourseMutation.mutate()}
                            >
                                {t('course.join.joinButton')}
                            </Button>
                        </Box>
                    </Grid>
                </Grid>
            </AbsolutelyCentered>
        </PageContainer>
    );
};
