import VisibilityOffOutlinedIcon from '@mui/icons-material/VisibilityOffOutlined';
import VisibilityOutlinedIcon from '@mui/icons-material/VisibilityOutlined';
import { Box, IconButton } from '@mui/material';
import { stylesRow } from 'common/styles';
import { ComponentProps, useState } from 'react';
import { FormInputField } from './FormInputField';

export const FormPasswordField = ({ textFieldProps, ...props }: ComponentProps<typeof FormInputField>) => {
    const [isVisible, setVisible] = useState(false);

    return (
        <Box sx={stylesRow}>
            <FormInputField
                {...props}
                type={isVisible ? 'text' : 'password'}
                textFieldProps={{ ...textFieldProps, required: false }}
            />
            <IconButton
                color="primary"
                sx={{ mt: 2.5, ml: 1 }}
                onClick={() => setVisible((visible) => !visible)}
            >
                {isVisible ? <VisibilityOffOutlinedIcon /> : <VisibilityOutlinedIcon />}
            </IconButton>
        </Box>
    );
};
