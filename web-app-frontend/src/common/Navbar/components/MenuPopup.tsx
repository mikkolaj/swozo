import { Menu } from '@mui/material';
import { ComponentProps, PropsWithChildren } from 'react';

type Props = {
    anchorEl: ComponentProps<typeof Menu>['anchorEl'];
    onHide: () => void;
};

export const AVATAR_WIDTH = 32;
export const AVATAR_HEIGHT = 32;

export const MenuPopup = ({ anchorEl, onHide, children }: PropsWithChildren<Props>) => {
    return (
        <Menu
            anchorEl={anchorEl}
            open={!!anchorEl}
            onClose={onHide}
            onClick={onHide}
            PaperProps={{
                elevation: 0,
                sx: {
                    overflow: 'visible',
                    filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))',
                    mt: 1.5,
                    '& .MuiAvatar-root': {
                        width: AVATAR_WIDTH,
                        height: AVATAR_HEIGHT,
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
            {children}
        </Menu>
    );
};
