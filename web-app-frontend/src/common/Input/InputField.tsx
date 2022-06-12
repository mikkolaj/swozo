import { Box, SxProps, TextField, TextFieldProps, Theme } from '@mui/material';
import { FieldHookConfig, useField } from 'formik';
import { ChangeEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { capitalized } from 'utils/utils';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
type Props = FieldHookConfig<any> & {
    labelPath?: string;
    labelText?: string;
    wrapperSx?: SxProps<Theme>;
    textFieldProps?: TextFieldProps;
    onChangeDecorator?: (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
};

export const InputField = ({
    labelPath,
    labelText,
    wrapperSx,
    textFieldProps,
    onChangeDecorator,
    children,
    ...props
}: Props) => {
    const [{ onChange, ...field }, meta] = useField(props);
    const { t } = useTranslation();

    return (
        <Box sx={wrapperSx}>
            <TextField
                label={labelPath ? capitalized(t(labelPath)) : labelText ?? ''}
                error={!!(meta.touched && meta.error)}
                helperText={meta.touched && meta.error}
                variant={textFieldProps?.variant ?? 'outlined'}
                onChange={(e) => {
                    onChange(e);
                    if (onChangeDecorator) onChangeDecorator(e);
                }}
                {...field}
                {...textFieldProps}
            >
                {children}
            </TextField>
        </Box>
    );
};
