import { Box, SxProps, TextField, TextFieldProps, Theme } from '@mui/material';
import _ from 'lodash';
import { useTranslation } from 'react-i18next';

type Props = {
    i18nLabel?: string;
    labelText?: string;
    wrapperSx?: SxProps<Theme>;
    value: string;
    textFieldProps?: TextFieldProps;
};

export const ReadonlyField = ({ i18nLabel, labelText, wrapperSx, value, textFieldProps }: Props) => {
    const { t } = useTranslation();
    const { InputProps, ...rest } = textFieldProps ?? {};
    return (
        <Box sx={wrapperSx}>
            <TextField
                label={i18nLabel ? _.capitalize(t(i18nLabel)) : labelText ?? ''}
                type={textFieldProps?.type}
                value={value}
                contentEditable={false}
                required={false}
                InputProps={{ readOnly: true, ...InputProps }}
                {...rest}
            />
        </Box>
    );
};
