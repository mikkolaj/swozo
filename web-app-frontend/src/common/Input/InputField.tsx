import { Box, SxProps, TextField, TextFieldProps, Theme } from '@mui/material';
import { FieldHookConfig, useField } from 'formik';
import { useTranslation } from 'react-i18next';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
type Props = FieldHookConfig<any> & {
    labelPath: string;
    wrapperSx?: SxProps<Theme>;
    textFieldProps?: TextFieldProps;
};

export const InputField: React.FC<Props> = ({ labelPath, wrapperSx, textFieldProps, ...props }: Props) => {
    const [field, meta] = useField(props);
    const { t } = useTranslation();

    const capitalized = (x: string) => x && x[0].toUpperCase() + x.slice(1);

    return (
        <Box sx={wrapperSx}>
            <TextField
                label={capitalized(t(labelPath))}
                error={!!(meta.touched && meta.error)}
                helperText={meta.touched && meta.error}
                variant={textFieldProps?.variant ?? 'standard'}
                {...field}
                {...textFieldProps}
            />
        </Box>
    );
};
