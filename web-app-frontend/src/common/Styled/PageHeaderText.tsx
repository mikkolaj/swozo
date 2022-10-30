import { Typography } from '@mui/material';
import { ComponentProps } from 'react';

type Props = ComponentProps<typeof Typography> & {
    text?: string;
};

export const PageHeaderText = ({ text, ...typographyProps }: Props) => {
    const { sx, ...rest } = typographyProps;
    return (
        <Typography variant="h4" sx={{ overflowX: 'hidden', textOverflow: 'ellipsis', ...sx }} {...rest}>
            {text}
        </Typography>
    );
};
