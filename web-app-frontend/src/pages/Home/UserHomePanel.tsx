import { Box, Button, Card, CardContent, Divider, Grid, Paper, Typography } from '@mui/material';
import { ActivitySummaryDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { Calendar } from 'common/Calendar/Calendar';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import { stylesRowWithSpaceBetweenItems } from 'common/styles';
import dayjs from 'dayjs';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import _ from 'lodash';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { formatDateTime } from 'utils/util';

export const UserHomePanel = () => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { pushApiError, removeApiError, isApiErrorSet } = useApiErrorHandling({});
    const [sortedActivities, setSortedActivities] = useState<ActivitySummaryDto[]>([]);

    const { data: userActivities } = useErrorHandledQuery(
        ['activities', 'summary'],
        () =>
            getApis().activitiesApi.getUserActivities({
                daysInThePast: 62,
                daysInTheFuture: 62,
            }),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    useEffect(() => {
        if (userActivities) {
            setSortedActivities(_.sortBy(userActivities, (activity) => activity.startTime));
        }
    }, [userActivities]);

    const [past, future] = _.partition(sortedActivities, ({ startTime }) => !dayjs().isBefore(startTime));

    return (
        <Box sx={{ padding: 0, marginX: '200px', marginTop: -5 }}>
            <Grid container spacing={10} sx={{ marginTop: 0 }}>
                <Grid item xs={6}>
                    <Card
                        sx={{
                            position: 'relative',
                            shadow: 3,
                        }}
                    >
                        <Calendar activities={userActivities ?? []} />
                        <Divider />
                        <Box sx={{ p: 2 }}>
                            <Typography variant="h6">{t('home.upcomingActivities')}</Typography>
                            {future.slice(0, 4).map(({ id, courseId, name, startTime }) => (
                                <Paper
                                    key={id}
                                    sx={{
                                        p: 1,
                                        m: 2,
                                        boxShadow: 2,
                                        display: 'flex',
                                        flexDirection: 'row',
                                        justifyContent: 'space-between',
                                    }}
                                >
                                    <LinkedTypography
                                        decorated
                                        sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }}
                                        to={PageRoutes.Course(courseId)}
                                        text={name}
                                    />
                                    <Typography>{formatDateTime(startTime)}</Typography>
                                </Paper>
                            ))}
                            {future.length === 0 && (
                                <Typography sx={{ p: 2, textAlign: 'center' }}>
                                    {t('home.noneUpcoming')}
                                </Typography>
                            )}
                        </Box>
                    </Card>
                </Grid>
                <Grid item xs={6}>
                    <Card sx={{ shadow: 3, position: 'relative' }}>
                        <CardContent sx={{ p: 0 }}>
                            <Typography sx={{ p: 2 }} variant="h6">
                                {t('home.activeCourses')}
                            </Typography>
                            <Divider />
                            {past.slice(0, 10).map(({ id, courseId, courseName, name }) => (
                                <Paper
                                    key={`${courseId}_${id}`}
                                    sx={{
                                        p: 1,
                                        m: 2,
                                        boxShadow: 2,
                                        ...stylesRowWithSpaceBetweenItems,
                                    }}
                                >
                                    <LinkedTypography
                                        decorated
                                        sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }}
                                        to={PageRoutes.Course(courseId)}
                                        text={courseName}
                                    />
                                    <Typography>{name}</Typography>
                                </Paper>
                            ))}
                            {past.length === 0 && (
                                <Typography sx={{ p: 2, textAlign: 'center' }}>
                                    {t('home.noneActive')}
                                </Typography>
                            )}
                            <Button
                                onClick={() => navigate(PageRoutes.MY_COURSES)}
                                sx={{ display: 'block', margin: 'auto' }}
                            >
                                {t('home.seeAll')}
                            </Button>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};
