import { Logout } from '@mui/icons-material';
import { Avatar, Divider, IconButton, ListItemIcon, Menu, MenuItem } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import React, { RefObject, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { logout } from 'services/features/auth/authSlice';
import { useAppDispatch } from 'services/store';
import { PageRoutes } from 'utils/routes';
import { NavbarItem } from './NavbarItem';

export const Navbar = () => {
    const { t } = useTranslation();
    const dispatch = useAppDispatch();
    const appBarRef: RefObject<HTMLDivElement> = useRef(null);
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

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
                    <Box display="flex" flexDirection="row" alignItems="center" justifyContent="center">
                        <NavbarItem textPath="navbar.courses" route={PageRoutes.COURSES} />
                        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small">
                            <Avatar sx={{ width: 32, height: 32 }}>D</Avatar>
                        </IconButton>
                    </Box>
                </Toolbar>
            </AppBar>
            <Box sx={{ height: appBarRef.current?.clientHeight }} />
            <Menu
                anchorEl={anchorEl}
                open={!!anchorEl}
                onClose={() => setAnchorEl(null)}
                onClick={() => setAnchorEl(null)}
                PaperProps={{
                    elevation: 0,
                    sx: {
                        overflow: 'visible',
                        filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))',
                        mt: 1.5,
                        '& .MuiAvatar-root': {
                            width: 32,
                            height: 32,
                            ml: -0.5,
                            mr: 1,
                        },
                        '&:before': {
                            content: '""',
                            display: 'block',
                            position: 'absolute',
                            top: 0,
                            right: 14,
                            width: 10,
                            height: 10,
                            bgcolor: 'background.paper',
                            transform: 'translateY(-50%) rotate(45deg)',
                            zIndex: 0,
                        },
                    },
                }}
                transformOrigin={{ horizontal: 'right', vertical: 'top' }}
                anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
                <MenuItem>
                    <Avatar /> Dominik Kowalski
                </MenuItem>
                <Divider />
                {/* {hasRole(authData, AuthDataRolesEnum.Teacher, AuthDataRolesEnum.TechnicalTeacher) &&
                    rolePreference === AuthDataRolesEnum.Teacher && (
                        <MenuItem>
                            <ListItemIcon>
                                <SwitchAccount fontSize="small" />
                            </ListItemIcon>
                            {t('navbar.menu.switchToTechnical')}
                        </MenuItem>
                    )}
                {hasRole(authData, AuthDataRolesEnum.Teacher, AuthDataRolesEnum.TechnicalTeacher) &&
                    rolePreference === AuthDataRolesEnum.TechnicalTeacher && (
                        <MenuItem>
                            <ListItemIcon>
                                <SwitchAccount fontSize="small" />
                            </ListItemIcon>
                            {t('navbar.menu.switchToTeacher')}
                        </MenuItem>
                    )} */}
                <MenuItem onClick={() => dispatch(logout())} sx={{ textTransform: 'capitalize' }}>
                    <ListItemIcon>
                        <Logout fontSize="small" />
                    </ListItemIcon>
                    {t('navbar.menu.logout')}
                </MenuItem>
            </Menu>
        </Box>
    );
};
