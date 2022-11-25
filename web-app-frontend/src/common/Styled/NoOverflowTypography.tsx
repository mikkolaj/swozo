import { Typography } from '@mui/material';
import { ComponentProps, PropsWithChildren } from 'react';

type Props = ComponentProps<typeof Typography>;

export const NoOverflowTypography = ({ sx, children, ...rest }: PropsWithChildren<Props>) => {
    return (
        <Typography sx={{ overflowX: 'hidden', textOverflow: 'ellipsis', ...sx }} {...rest}>
            {children}
        </Typography>
    );
};
