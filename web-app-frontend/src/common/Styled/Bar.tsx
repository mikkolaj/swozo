import { Box } from '@mui/material';
import React, { FC } from 'react';

export const Bar: FC<React.ComponentProps<typeof Box>> = (sx, ...props) => {
    return (
        <>
            <Box
                sx={{
                    borderBottom: '1px solid rgba(0, 0, 0, 0.5)',
                    position: 'absolute',
                    left: 0,
                    width: '100%',
                    marginTop: '10px',
                    ...sx,
                }}
                {...props}
            />
        </>
    );
};
