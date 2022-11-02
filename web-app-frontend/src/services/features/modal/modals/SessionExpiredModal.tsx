// import CheckIcon from '@mui/icons-material/Check';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import { Box, Typography } from '@mui/material';
import { stylesColumnCenteredVertical, stylesColumnFullyCentered, stylesRow } from 'common/styles';
import { useTranslation } from 'react-i18next';
import { CenteredModal } from './CenteredModal';
type Props = {
    onClose: () => void;
};

export const SessionExpiredModal = ({ onClose }: Props) => {
    const { t } = useTranslation();

    return (
        <CenteredModal
            onClose={onClose}
            cardSx={{
                ...stylesColumnFullyCentered,
                p: 2,
                justifyContent: 'center',
            }}
        >
            <Box sx={{ ...stylesColumnCenteredVertical, textAlign: 'center' }}>
                <Box sx={{ ...stylesRow, justifyContent: 'center', mb: 2 }}>
                    <AccessTimeIcon color="info" sx={{ fontSize: 80 }} />
                </Box>
                <Typography variant="h5">{t('login.sessionExpired.info')}</Typography>
            </Box>
        </CenteredModal>
    );
};
