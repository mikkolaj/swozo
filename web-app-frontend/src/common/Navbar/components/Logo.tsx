import { Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';

export const Logo = () => {
    const { t } = useTranslation();

    return (
        <Typography
            variant="h6"
            component={Link}
            to={PageRoutes.HOME}
            color="inherit"
            sx={{ marginRight: 'auto', textDecoration: 'none' }}
        >
            {t('navbar.logo')}
        </Typography>
    );
};
