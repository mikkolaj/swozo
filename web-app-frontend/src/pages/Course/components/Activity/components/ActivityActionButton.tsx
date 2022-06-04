import { Box, Button } from '@mui/material';
import { FC } from 'react';
import { useTranslation } from 'react-i18next';

type Props = React.ComponentProps<typeof Button> & {
    textPath: string;
};

export const ActivityActionButton: FC<Props> = ({ textPath, ...buttonProps }: Props) => {
    const { t } = useTranslation();

    return (
        <Box>
            <Button sx={{ justifyContent: 'flex-start' }} {...buttonProps}>
                {t(textPath)}
            </Button>
        </Box>
    );
};
