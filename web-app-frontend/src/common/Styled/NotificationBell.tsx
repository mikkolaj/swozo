import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import { Badge, Box, IconButton, Paper, Typography } from '@mui/material';
import { MenuPopup } from 'common/Navbar/components/MenuPopup';
import { useState } from 'react';

type Props = {
    notifications: string[];
};

export const NotificationBell = ({ notifications }: Props) => {
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    return (
        <Box>
            <IconButton size="large" onClick={(e) => setAnchorEl(e.currentTarget)} sx={{ color: 'white' }}>
                <Badge badgeContent={notifications.length} color="secondary">
                    <NotificationsRoundedIcon color="inherit" />
                </Badge>
            </IconButton>
            <MenuPopup anchorEl={anchorEl} onHide={() => setAnchorEl(null)}>
                {notifications.length > 0 ? (
                    notifications.map((notification, idx) => (
                        <Paper key={idx} sx={{ p: 2, mx: 1, mt: idx > 0 ? 2 : 0 }}>
                            <Typography
                                sx={{ overflowX: 'hidden', textOverflow: 'ellipsis', maxWidth: '400px' }}
                            >
                                {notification}
                            </Typography>
                        </Paper>
                    ))
                ) : (
                    <Paper sx={{ p: 2, mx: 1 }}>
                        <Typography sx={{ overflowX: 'hidden', textOverflow: 'ellipsis', maxWidth: '400px' }}>
                            nie masz powiadomień
                            {/* TODO */}
                        </Typography>
                    </Paper>
                )}
            </MenuPopup>
        </Box>
    );
};
