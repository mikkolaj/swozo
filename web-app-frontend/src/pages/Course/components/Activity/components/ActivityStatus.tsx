import CancelIcon from '@mui/icons-material/Cancel';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import InsertInvitationIcon from '@mui/icons-material/InsertInvitation';
import { Tooltip } from '@mui/material';
import { ActivityDetailsDto } from 'api';
import dayjs from 'dayjs';
import { useTranslation } from 'react-i18next';

type Props = {
    activity: ActivityDetailsDto;
};

export const ActivityStatus = ({ activity }: Props) => {
    const { t } = useTranslation();
    return (
        <>
            {activity.cancelled ? (
                <Tooltip title={t('course.activity.cancelled')}>
                    <CancelIcon color="error" fontSize="large" />
                </Tooltip>
            ) : dayjs().isAfter(activity.endTime) ? (
                <Tooltip title={t('course.activity.finished')}>
                    <CheckCircleIcon color="success" fontSize="large" />
                </Tooltip>
            ) : (
                <Tooltip title={t('course.activity.notStarted')}>
                    <InsertInvitationIcon color="primary" fontSize="large" />
                </Tooltip>
            )}
        </>
    );
};
