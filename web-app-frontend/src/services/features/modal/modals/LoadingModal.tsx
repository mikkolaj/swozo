import { Box, CircularProgress, Typography } from '@mui/material';
import { stylesColumnCenteredVertical, stylesColumnFullyCentered } from 'common/styles';
import { CenteredModal } from './CenteredModal';

type Props = {
    onClose: () => void;
    textLines: string[];
};

export const LoadingModal = ({ onClose, textLines }: Props) => {
    return (
        <CenteredModal
            onClose={onClose}
            cardSx={{
                ...stylesColumnFullyCentered,
                p: 2,
                justifyContent: 'center',
            }}
        >
            <CircularProgress size={80} thickness={3} sx={{ animationDuration: '1500ms' }} />
            <Box sx={{ ...stylesColumnCenteredVertical, mt: 2, textAlign: 'center' }}>
                {textLines.map((line, idx) => (
                    <Typography key={idx}>{line}</Typography>
                ))}
            </Box>
        </CenteredModal>
    );
};
