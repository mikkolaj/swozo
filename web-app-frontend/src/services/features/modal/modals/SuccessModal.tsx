// import CheckIcon from '@mui/icons-material/Check';
import CheckCircleRoundedIcon from '@mui/icons-material/CheckCircleRounded';
import { Box, Typography } from '@mui/material';
import { stylesColumnCenteredVertical, stylesColumnFullyCentered, stylesRow } from 'common/styles';
import { CenteredModal } from './CenteredModal';
type Props = {
    onClose: () => void;
    textLines: string[];
};

export const SuccessModal = ({ onClose, textLines }: Props) => {
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
                    <CheckCircleRoundedIcon color="success" sx={{ fontSize: 80 }} />
                </Box>
                {textLines.map((line, idx) => (
                    <Typography sx={{ mt: idx > 0 ? 1 : 0 }} key={idx}>
                        {line}
                    </Typography>
                ))}
            </Box>
        </CenteredModal>
    );
};
