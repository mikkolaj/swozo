import { Logout, SwitchAccount } from '@mui/icons-material';
import { Avatar, Divider, IconButton, ListItemIcon, MenuItem } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import { getApis } from 'api/initialize-apis';
import { stylesRowFullyCentered } from 'common/styles';
import { useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import { logout, setRolePreference } from 'services/features/auth/authSlice';
import { useAppDispatch } from 'services/store';
import { STUDENT, TEACHER, TECHNICAL_TEACHER, WithPreference, WithRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { formatName } from 'utils/util';
import { Logo } from './components/Logo';
import { AVATAR_HEIGHT, AVATAR_WIDTH, MenuPopup } from './components/MenuPopup';
import { NavbarItem } from './NavbarItem';

export const Navbar = () => {
    const { t } = useTranslation();
    const dispatch = useAppDispatch();
    const appBarRef = useRef<HTMLDivElement>(null);
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const { data: me } = useQuery('me', () => getApis().userApi.getUserInfo());

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar ref={appBarRef} position="fixed">
                <Toolbar sx={{ mr: 1 }}>
                    <Logo />
                    <Box sx={stylesRowFullyCentered}>
                        <WithRole roles={[STUDENT]}>
                            <NavbarItem textI18n="navbar.myFiles" route={PageRoutes.FILES} />
                        </WithRole>
                        <WithPreference role={TECHNICAL_TEACHER}>
                            <NavbarItem textI18n="navbar.myModules" route={PageRoutes.MY_MODULES} />
                        </WithPreference>
                        <NavbarItem textI18n="navbar.myCourses" route={PageRoutes.MY_COURSES} />
                        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small">
                            <Avatar sx={{ width: AVATAR_WIDTH, height: AVATAR_HEIGHT }}>
                                {me?.name[0]?.toUpperCase() ?? '?'}
                            </Avatar>
                        </IconButton>
                    </Box>
                </Toolbar>
            </AppBar>
            <Box sx={{ height: appBarRef.current?.clientHeight }} />

            <MenuPopup anchorEl={anchorEl} onHide={() => setAnchorEl(null)}>
                <MenuItem>
                    <Avatar /> {formatName(me?.name, me?.surname)}
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
            </MenuPopup>
        </Box>
    );
};
