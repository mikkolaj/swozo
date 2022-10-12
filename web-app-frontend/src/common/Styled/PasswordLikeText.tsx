import VisibilityOffOutlinedIcon from '@mui/icons-material/VisibilityOffOutlined';
import VisibilityOutlinedIcon from '@mui/icons-material/VisibilityOutlined';
import { Box, IconButton, SxProps, Typography } from '@mui/material';
import { Theme } from '@mui/system';
import { ComponentProps, useState } from 'react';

type Props = ComponentProps<typeof Typography> & {
    textSupplier: (isVisible: boolean) => string;
    wrapperSx?: SxProps<Theme>;
    visibleByDefault?: boolean;
};

export const PasswordLikeText = ({
    visibleByDefault = false,
    textSupplier,
    wrapperSx,
    sx: typographySx,
    ...props
}: Props) => {
    const [isVisible, setVisible] = useState(visibleByDefault);

    return (
        <Box sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center', ...wrapperSx }}>
            <Typography sx={{ ...typographySx }} {...props}>
                {textSupplier(isVisible)}
            </Typography>
            <IconButton color="primary" onClick={() => setVisible((visible) => !visible)}>
                {isVisible ? <VisibilityOffOutlinedIcon /> : <VisibilityOutlinedIcon />}
            </IconButton>
        </Box>
    );
};
