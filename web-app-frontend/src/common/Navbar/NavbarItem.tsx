import { Box, Button, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';

type Props = {
    textI18n: string;
    route?: string;
    onClick?: () => unknown;
};

export const NavbarItem = ({ textI18n, route, onClick }: Props) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <Button
            onClick={() => {
                if (onClick !== undefined) onClick();
                else if (route !== undefined) navigate(route);
            }}
            color="inherit"
        >
            <Box
                sx={{
                    borderBottom: location.pathname === route ? '1px solid rgba(255,255,255, 0.8)' : 'none',
                }}
            >
                <Typography component="h1" variant="button">
                    {t(textI18n)}
                </Typography>
            </Box>
        </Button>
    );
};
