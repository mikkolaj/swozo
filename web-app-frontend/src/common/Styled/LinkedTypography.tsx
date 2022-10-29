import { Typography } from '@mui/material';
import { blue } from '@mui/material/colors';
import { ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';

type Props = ComponentProps<typeof Typography> & {
    to: string;
    textI18n?: string;
    text?: string;
    decorated?: boolean;
};

export const LinkedTypography = ({ to, textI18n, text, decorated = false, ...props }: Props) => {
    const { t } = useTranslation();
    const { sx, ...rest } = props;

    return (
        <Typography
            sx={{
                color: decorated ? blue[700] : undefined,
                ':hover': { color: decorated ? blue[900] : undefined },
                ...sx,
            }}
            {...rest}
        >
            <Link style={{ textDecoration: 'none', color: 'inherit' }} to={to}>
                {text ?? (textI18n && t(textI18n))}
            </Link>
        </Typography>
    );
};
