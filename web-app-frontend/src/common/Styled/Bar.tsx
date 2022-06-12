import { Box } from '@mui/material';
import { ComponentProps } from 'react';

export const Bar = ({ sx, ...props }: ComponentProps<typeof Box>) => {
    return (
        <Box sx={sx}>
            <Box
                sx={{
                    borderBottom: '1px solid rgba(0, 0, 0, 0.5)',
                    position: 'absolute',
                    left: 0,
                    width: '100%',
                }}
                {...props}
            />
        </Box>
    );
};
