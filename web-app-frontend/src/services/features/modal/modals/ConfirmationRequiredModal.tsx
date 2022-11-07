import QuestionMarkIcon from '@mui/icons-material/QuestionMark';
import { Box, Button, Typography } from '@mui/material';
import {
    stylesColumnCenteredVertical,
    stylesColumnFullyCentered,
    stylesRow,
    stylesRowWithSpaceBetweenItems,
} from 'common/styles';
import { CenteredModal } from './CenteredModal';
type Props = {
    open: boolean;
    onClose: () => void;
    onYes: () => void;
    yesPreffered?: boolean;
    yesText: string;
    noText: string;
    textLines: string[];
};

export const ConfirmationRequiredModal = ({
    open,
    onClose,
    onYes,
    yesText,
    noText,
    textLines,
    yesPreffered = true,
}: Props) => {
    return (
        <CenteredModal
            open={open}
            onClose={onClose}
            cardSx={{
                ...stylesColumnFullyCentered,
                p: 2,
                justifyContent: 'center',
            }}
        >
            <Box sx={{ ...stylesColumnCenteredVertical, textAlign: 'center' }}>
                <Box sx={{ ...stylesRow, justifyContent: 'center', mb: 2 }}>
                    <QuestionMarkIcon sx={{ fontSize: 80 }} />
                </Box>
                {textLines.map((line, idx) => (
                    <Typography sx={{ mt: idx > 0 ? 1 : 0 }} key={idx}>
                        {line}
                    </Typography>
                ))}
            </Box>
            <Box sx={{ ...stylesRowWithSpaceBetweenItems, width: '70%', mt: 2 }}>
                <Button
                    sx={{ minWidth: '100px' }}
                    onClick={yesPreffered ? onClose : onYes}
                    variant="outlined"
                >
                    {yesPreffered ? noText : yesText}
                </Button>
                <Button
                    sx={{ minWidth: '100px' }}
                    onClick={yesPreffered ? onYes : onClose}
                    variant="contained"
                >
                    {yesPreffered ? yesText : noText}
                </Button>
            </Box>
        </CenteredModal>
    );
};
