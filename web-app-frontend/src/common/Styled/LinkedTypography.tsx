import { Typography } from '@mui/material';
import { ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';

type Props = ComponentProps<typeof Typography> & {
    to: string;
    textI18n?: string;
    text?: string;
};

export const LinkedTypography = ({ to, textI18n, text, ...props }: Props) => {
    const { t } = useTranslation();

    return (
        <Typography {...props}>
            <Link style={{ textDecoration: 'none', color: 'inherit' }} to={to}>
                {text ?? (textI18n && t(textI18n))}
            </Link>
        </Typography>
    );
};
