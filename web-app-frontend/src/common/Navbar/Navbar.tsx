import { Logout, SwitchAccount } from '@mui/icons-material';
import { Avatar, Divider, IconButton, ListItemIcon, MenuItem } from '@mui/material';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import { NotificationBell } from 'common/Styled/NotificationBell';
import { stylesRowFullyCentered } from 'common/styles';
import { useMeQuery } from 'hooks/query/useMeQuery';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { logout, setRolePreference } from 'services/features/auth/authSlice';
import { useAppDispatch } from 'services/store';
import {
    ADMIN,
    ANY_BUT_NOT_ADMIN,
    STUDENT,
    TEACHER,
    TECHNICAL_TEACHER,
    WithPreference,
    WithRole,
} from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { formatName } from 'utils/util';
import { Logo } from './components/Logo';
import { AVATAR_HEIGHT, AVATAR_WIDTH, MenuPopup } from './components/MenuPopup';
import { NavbarItem } from './NavbarItem';

export const HEIGHT = 64;

export const Navbar = () => {
    const { t } = useTranslation();
    const dispatch = useAppDispatch();
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
    const { me } = useMeQuery();

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar position="fixed" sx={{ height: HEIGHT, top: 0, left: 0 }}>
                <Toolbar sx={{ mr: 1 }}>
                    <Logo />
                    <Box sx={stylesRowFullyCentered}>
                        <WithRole roles={[STUDENT]}>
                            <NavbarItem textI18n="navbar.myFiles" route={PageRoutes.FILES} />
                        </WithRole>
                        <WithRole roles={[TEACHER]}>
                            <NavbarItem textI18n="navbar.publicModules" route={PageRoutes.PUBLIC_MODULES} />
                        </WithRole>
                        <WithPreference role={TECHNICAL_TEACHER}>
                            <NavbarItem textI18n="navbar.myModules" route={PageRoutes.MY_MODULES} />
                        </WithPreference>
                        <WithRole roles={[STUDENT]}>
                            <NavbarItem textI18n="navbar.publicCourses" route={PageRoutes.PUBLIC_COURSES} />
                        </WithRole>
                        <WithRole roles={ANY_BUT_NOT_ADMIN}>
                            <NavbarItem textI18n="navbar.myCourses" route={PageRoutes.MY_COURSES} />
                            <NotificationBell notifications={[]} />
                        </WithRole>
                        <WithRole roles={[ADMIN]}>
                            <NavbarItem
                                textI18n="navbar.virtualMachines"
                                route={PageRoutes.VIRTUAL_MACHINES}
                            />
                            <NavbarItem textI18n="navbar.userManagement" route={PageRoutes.HOME} />
                        </WithRole>

                        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small">
                            <Avatar sx={{ width: AVATAR_WIDTH, height: AVATAR_HEIGHT }}>
                                {me?.name[0]?.toUpperCase() ?? '?'}
                            </Avatar>
                        </IconButton>
                    </Box>
                </Toolbar>
            </AppBar>
            <Box sx={{ height: HEIGHT, width: '100%' }} />

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
