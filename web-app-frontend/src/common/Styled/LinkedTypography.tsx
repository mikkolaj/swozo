import { Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';

type Props = React.ComponentProps<typeof Typography> & {
    to: string;
    textPath?: string;
    text?: string;
};

export const LinkedTypography: React.FC<Props> = ({ to, textPath, text, ...props }: Props) => {
    const { t } = useTranslation();

    return (
        <Typography {...props}>
            <Link style={{ textDecoration: 'none', color: 'inherit' }} to={to}>
                {text ?? (textPath && t(textPath))}
            </Link>
        </Typography>
    );
};
