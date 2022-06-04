import { Box, Button, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';

type Props = {
    textPath: string;
    route?: string;
    onClick?: () => unknown;
};

export const NavbarItem: React.FC<Props> = ({ textPath, route, onClick }: Props) => {
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
            <Box sx={{ textDecoration: location.pathname === route ? 'underline' : 'none' }}>
                <Typography component="h1" variant="button">
                    {t(textPath)}
                </Typography>
            </Box>
        </Button>
    );
};
