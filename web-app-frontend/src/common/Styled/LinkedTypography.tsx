import { Typography } from '@mui/material';
import { ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';

type Props = ComponentProps<typeof Typography> & {
    to: string;
    textPath?: string;
    text?: string;
};

export const LinkedTypography = ({ to, textPath, text, ...props }: Props) => {
    const { t } = useTranslation();

    return (
        <Typography {...props}>
            <Link style={{ textDecoration: 'none', color: 'inherit' }} to={to}>
                {text ?? (textPath && t(textPath))}
            </Link>
        </Typography>
    );
};
