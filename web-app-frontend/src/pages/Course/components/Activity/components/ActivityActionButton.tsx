import { Box, Button } from '@mui/material';
import { ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';

type Props = ComponentProps<typeof Button> & {
    textI18n: string;
};

export const ActivityActionButton = ({ textI18n, ...buttonProps }: Props) => {
    const { t } = useTranslation();

    return (
        <Box>
            <Button sx={{ justifyContent: 'flex-start' }} {...buttonProps}>
                {t(textI18n)}
            </Button>
        </Box>
    );
};
