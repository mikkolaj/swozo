import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { RefObject, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { logout } from 'services/features/auth/authSlice';
import { useAppDispatch } from 'services/store';
import { PageRoutes } from 'utils/routes';
import { NavbarItem } from './NavbarItem';

const ResponsiveAppBar = () => {
    const { t } = useTranslation();
    const dispatch = useAppDispatch();
    const appBarRef: RefObject<HTMLDivElement> = useRef(null);

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar ref={appBarRef} position="fixed">
                <Toolbar sx={{ mr: 1 }}>
                    <Typography
                        variant="h6"
                        component={Link}
                        to={PageRoutes.HOME}
                        color="inherit"
                        sx={{ flexGrow: 1, textDecoration: 'none' }}
                    >
                        {t('navbar.logo')}
                    </Typography>
                    <Box>
                        <NavbarItem textPath="navbar.courses" route={PageRoutes.COURSES} />
                        <NavbarItem textPath="navbar.logout" onClick={() => dispatch(logout())}></NavbarItem>
                    </Box>
                </Toolbar>
            </AppBar>
            <Box sx={{ height: appBarRef.current?.clientHeight }} />
        </Box>
    );
};
export default ResponsiveAppBar;
