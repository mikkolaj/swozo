import SchoolIcon from '@mui/icons-material/School';
import { Box, Button, Grid, TextField, Typography } from '@mui/material';
import { ApiError, ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import {
    stylesRow,
    stylesRowCenteredHorizontal,
    stylesRowCenteredVertical,
    stylesRowWithSpaceBetweenItems,
} from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { buildErrorHandler, HandlerConfig, useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useRequiredParams } from 'hooks/useRequiredParams';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { PageRoutes } from 'utils/routes';
import { AlreadyJoinedError, InvalidJoinUUidError } from './ErrorHandlers';
import { updateCourseCache } from './utils';

export const JoinCourseView = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [joinUUID] = useRequiredParams(['joinUUID']);
    const [password, setPassword] = useState('');
    const [invalidPasswordError, setInvalidPasswordError] = useState(false);

    const [errorHandlers, setErrorHandlers] = useState<HandlerConfig>({
        [ErrorType.COURSE_NOT_FOUND]: buildErrorHandler(() => <InvalidJoinUUidError />),
        [ErrorType.INVALID_COURSE_PASSWORD]: buildErrorHandler(() => setInvalidPasswordError(true), false),
    });

    const { isApiError, errorHandler, isApiErrorSet, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling(errorHandlers);

    const { data: userCourses } = useErrorHandledQuery(
        ['courses'],
        () => getApis().courseApi.getUserCourses(),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    const { data: course } = useErrorHandledQuery(
        ['courses', 'summary', joinUUID],
        () => getApis().courseApi.getPublicCourseData({ uuid: joinUUID }),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    const joinCourseMutation = useMutation(
        () => {
            if (invalidPasswordError) {
                setInvalidPasswordError(false);
            }

            return getApis().courseApi.joinCourse({
                joinCourseRequest: { joinUUID, password: course?.isPasswordProtected ? password : undefined },
            });
        },
        {
            onSuccess: (course) => {
                updateCourseCache(queryClient, course);
                toast.success(t('course.join.joinedMessage'));
                navigate(PageRoutes.Course(course.id));
            },
            onError: (error) => {
                pushApiError(error as ApiError);
            },
        }
    );

    useEffect(() => {
        const newError = { errorType: ErrorType.ALREADY_A_MEMBER };
        const sameCourseDetails = userCourses?.find((course) => course.joinUUID === joinUUID);

        if (sameCourseDetails && !isApiErrorSet(newError)) {
            setErrorHandlers((handlers) => ({
                ...handlers,
                [ErrorType.ALREADY_A_MEMBER]: buildErrorHandler(() => (
                    <AlreadyJoinedError courseDetails={sameCourseDetails} />
                )),
            }));
            pushApiError(newError);
        }
    }, [userCourses, joinUUID, setErrorHandlers, isApiErrorSet, pushApiError]);

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
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
                                error={invalidPasswordError}
                                helperText={
                                    invalidPasswordError ? t('course.join.error.invalidPassword') : ''
                                }
                                onKeyDown={(e) => {
                                    if (e.nativeEvent.key === 'Enter' && password) {
                                        joinCourseMutation.mutate();
                                    }
                                }}
                                sx={{ width: 180, mt: 5 }}
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
