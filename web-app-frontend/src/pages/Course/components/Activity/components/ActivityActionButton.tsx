import { Box, Button } from '@mui/material';
import { ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';

type Props = ComponentProps<typeof Button> & {
    textPath: string;
};

export const ActivityActionButton = ({ textPath, ...buttonProps }: Props) => {
    const { t } = useTranslation();

    return (
        <Box>
            <Button sx={{ justifyContent: 'flex-start' }} {...buttonProps}>
                {t(textPath)}
            </Button>
        </Box>
    );
};
