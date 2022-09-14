import { Box, SxProps, TextField, TextFieldProps, Theme } from '@mui/material';
import { FieldHookConfig, useField } from 'formik';
import _ from 'lodash';
import { ChangeEvent } from 'react';
import { useTranslation } from 'react-i18next';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
type Props = FieldHookConfig<any> & {
    i18nLabel?: string;
    labelText?: string;
    wrapperSx?: SxProps<Theme>;
    textFieldProps?: TextFieldProps;
    onChangeDecorator?: (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void;
};

export const InputField = ({
    i18nLabel,
    labelText,
    wrapperSx,
    textFieldProps,
    onChangeDecorator,
    children,
    type,
    ...props
}: Props) => {
    const [{ onChange, ...field }, meta] = useField(props);
    const { t } = useTranslation();

    return (
        <Box sx={wrapperSx}>
            <TextField
                label={i18nLabel ? _.capitalize(t(i18nLabel)) : labelText ?? ''}
                type={type}
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
