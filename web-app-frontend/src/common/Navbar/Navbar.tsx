import { Logout, SwitchAccount } from '@mui/icons-material';
import { Avatar, Divider, IconButton, ListItemIcon, Menu, MenuItem } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { getApis } from 'api/initialize-apis';
import { stylesRowFullyCentered } from 'common/styles';
import { RefObject, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import { Link } from 'react-router-dom';
import { logout, setRolePreference } from 'services/features/auth/authSlice';
import { useAppDispatch } from 'services/store';
import { STUDENT, TEACHER, TECHNICAL_TEACHER, WithPreference, WithRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { NavbarItem } from './NavbarItem';

export const Navbar = () => {
    const { t } = useTranslation();
    const dispatch = useAppDispatch();
    const appBarRef: RefObject<HTMLDivElement> = useRef(null);
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const { data: me } = useQuery('me', () => getApis().userApi.getUserInfo());

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar ref={appBarRef} position="fixed">
                <Toolbar sx={{ mr: 1 }}>
                    <Typography
                        variant="h6"
                        component={Link}
                        to={PageRoutes.HOME}
                        color="inherit"
                        sx={{ marginRight: 'auto', textDecoration: 'none' }}
                    >
                        {t('navbar.logo')}
                    </Typography>
                    <Box sx={stylesRowFullyCentered}>
                        <WithRole roles={[STUDENT]}>
                            <NavbarItem textPath="navbar.myFiles" route={PageRoutes.FILES} />
                        </WithRole>
                        <WithPreference role={TECHNICAL_TEACHER}>
                            <NavbarItem textPath="navbar.myModules" route={PageRoutes.MY_MODULES} />
                        </WithPreference>
                        <NavbarItem textPath="navbar.myCourses" route={PageRoutes.MY_COURSES} />
                        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small">
                            <Avatar sx={{ width: 32, height: 32 }}>{me?.name[0] ?? '?'}</Avatar>
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
                    <Avatar /> {me?.name} {me?.surname}
                </MenuItem>
                <Divider />

                <WithRole roles={[TECHNICAL_TEACHER]}>
                    <WithPreference role={TEACHER}>
                        <MenuItem onClick={() => dispatch(setRolePreference(TECHNICAL_TEACHER))}>
                            <ListItemIcon>
                                <SwitchAccount fontSize="small" />
                            </ListItemIcon>
                            {t('navbar.menu.switchToTechnical')}
                        </MenuItem>
                    </WithPreference>
                </WithRole>

                <WithRole roles={[TEACHER]}>
                    <WithPreference role={TECHNICAL_TEACHER}>
                        <MenuItem onClick={() => dispatch(setRolePreference(TEACHER))}>
                            <ListItemIcon>
                                <SwitchAccount fontSize="small" />
                            </ListItemIcon>
                            {t('navbar.menu.switchToTeacher')}
                        </MenuItem>
                    </WithPreference>
                </WithRole>

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
