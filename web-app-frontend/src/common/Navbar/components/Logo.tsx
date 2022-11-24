import { Typography } from '@mui/material';
import { AuthDetailsDtoRolesEnum } from 'api';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { useAppSelector } from 'services/store';
import { hasRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';

export const Logo = () => {
    const { t } = useTranslation();
    const auth = useAppSelector((state) => state.auth.authData);

    return (
        <Typography
            variant="h6"
            component={Link}
            to={PageRoutes.HOME}
            color="inherit"
            sx={{ marginRight: 'auto', textDecoration: 'none' }}
        >
            {hasRole(auth, AuthDetailsDtoRolesEnum.Admin) ? t('navbar.adminLogo') : t('navbar.logo')}
        </Typography>
    );
};
