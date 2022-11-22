import { Box, Button, Container, Typography } from '@mui/material';
import { ActivitySummaryDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { NoOverflowTypography } from 'common/Styled/NoOverflowTypography';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesColumn, stylesRow, stylesRowCenteredVertical } from 'common/styles';
import { useCancelActivity } from 'hooks/query/useCancelActivity';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import _ from 'lodash';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQueryClient } from 'react-query';
import { ConfirmationRequiredModal } from 'services/features/modal/modals/ConfirmationRequiredModal';
import { formatDate, formatTime } from 'utils/util';

export const UpcomingActivitiesList = () => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();
    const { pushApiError, removeApiError, isApiErrorSet } = useApiErrorHandling({});
    const [confirmationModalOpen, setConfirmationModalOpen] = useState(false);
    const [confirmationModalYesCallback, setConfirmationModalYesCallback] = useState<() => void>(
        () => undefined
    );
    // TODO paginations
    const { data: activities } = useErrorHandledQuery(
        ['activities', 'admin'],
        () => getApis().activitiesApi.getAllNotCancelledFutureActivitiesInRange({ daysInTheFuture: 31 }),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    const { cancelActivityMutation } = useCancelActivity(
        (activity) =>
            queryClient.setQueryData(['activities', 'admin'], (activities: ActivitySummaryDto[] = []) => {
                const removedActivity = activities.find(({ id }) => id !== activity.id);
                return !removedActivity
                    ? activities
                    : [
                          ...activities.filter((act) => act !== removedActivity),
                          { ...removedActivity, cancelled: true },
                      ];
            }),
        pushApiError
    );

    return (
        <PageContainer
            header={
                <Box sx={{ ...stylesRowCenteredVertical }}>
                    <PageHeaderText text={t('admin.activities.header')} />
                </Box>
            }
        >
            <Container>
                <StackedList
                    /* eslint-disable react/jsx-key */
                    header={
                        <StackedListHeader
                            proportions={[4, 4, 2, 2]}
                            items={['name', 'teacher', 'date'].map((label) => (
                                <Typography variant="body1" color="GrayText">
                                    {t(`admin.activities.list.headers.${label}`)}
                                </Typography>
                            ))}
                        />
                    }
                    content={
                        <StackedListContent
                            proportions={[4, 4, 2, 2]}
                            emptyItemsComponent={
                                <Box sx={{ ...stylesColumn, pt: 4, alignItems: 'center' }}>
                                    <Typography variant="h4">{t('admin.activities.list.empty')}</Typography>
                                </Box>
                            }
                            items={_.sortBy(activities ?? [], (activity) => activity.startTime)}
                            itemKeyExtractor={({ id }) => id}
                            itemRenderer={({ id, name, teacher, startTime, endTime, cancelled }) => [
                                <NoOverflowTypography variant="body1">{name}</NoOverflowTypography>,
                                <NoOverflowTypography variant="body1">
                                    {t('admin.activities.list.teacher', {
                                        firstName: teacher.name,
                                        lastName: teacher.surname,
                                        email: teacher.email,
                                    })}
                                </NoOverflowTypography>,
                                <NoOverflowTypography variant="body1">
                                    {t('admin.activities.list.timeRange', {
                                        date: formatDate(startTime),
                                        start: formatTime(startTime),
                                        end: formatTime(endTime),
                                    })}
                                </NoOverflowTypography>,
                                <Box sx={{ ml: 'auto', ...stylesRow }}>
                                    <Button
                                        color="error"
                                        disabled={cancelled}
                                        onClick={() => {
                                            setConfirmationModalYesCallback(
                                                () => () => cancelActivityMutation.mutate(id)
                                            );
                                            setConfirmationModalOpen(true);
                                        }}
                                    >
                                        {t('admin.activities.list.cancel')}
                                    </Button>
                                </Box>,
                            ]}
                        />
                    }
                    /* eslint-enable react/jsx-key */
                />
            </Container>
            <ConfirmationRequiredModal
                open={confirmationModalOpen}
                noText={t('commonModals.no')}
                yesText={t('commonModals.yes')}
                onClose={() => setConfirmationModalOpen(false)}
                onYes={() => {
                    setConfirmationModalOpen(false);
                    confirmationModalYesCallback();
                }}
                textLines={[t('admin.activities.list.areYouSure')]}
                yesPreffered={false}
            />
        </PageContainer>
    );
};
