import { Box } from '@mui/material';
import { ParameterDescription, ParameterDescriptionTypeEnum } from 'api';
import { FileInputButton } from 'common/Input/FileInputButton';
import { FormInputField } from 'common/Input/FormInputField';
import { stylesRowCenteredVertical } from 'common/styles';
import { i18n } from 'i18next';
import { getTranslated } from 'utils/util';
import { InputFieldUtils } from '../utils';

type FieldProvider = (param: ParameterDescription, i18n: i18n, fieldUtils: InputFieldUtils) => JSX.Element;

type InputFieldFactory = Record<ParameterDescriptionTypeEnum, FieldProvider>;

export const inputFieldFactory = (): InputFieldFactory => {
    return {
        FILE: (
            { name, required, translatedLabel, clientValidationHelpers }: ParameterDescription,
            i18n,
            { setFieldValue, setAssociatedValue, errorMessage }
        ) => (
            <Box sx={stylesRowCenteredVertical}>
                <FormInputField
                    name={name}
                    wrapperSx={{ mt: 0 }}
                    type="text"
                    labelText={getTranslated(i18n, translatedLabel)}
                    textFieldProps={{
                        inputProps: { readOnly: true },
                        required,
                        error: !!errorMessage,
                        helperText: errorMessage,
                    }}
                />
                <FileInputButton
                    text={i18n.t('dynamicForm.file.button')}
                    allowedExtensions={clientValidationHelpers?.['allowedExtensions'] as string[]}
                    onFilesSelected={(files) => {
                        if (files && files.length > 0) {
                            setFieldValue(files[0].name, ParameterDescriptionTypeEnum.File);
                            setAssociatedValue?.(files[0]);
                        } else if (!files) {
                            setFieldValue('', ParameterDescriptionTypeEnum.File);
                            setAssociatedValue?.(undefined);
                        }
                    }}
                />
            </Box>
        ),
        TEXT: (
            { name, required, translatedLabel }: ParameterDescription,
            i18n,
            { setFieldValue, errorMessage, setAssociatedValue }
        ) => (
            <FormInputField
                wrapperSx={{ mt: 0, width: '50%' }}
                textFieldProps={{
                    fullWidth: true,
                    required,
                    onChange: (e) => {
                        setFieldValue(e.target.value, ParameterDescriptionTypeEnum.Text);
                        setAssociatedValue?.(e.target.value);
                    },
                    error: !!errorMessage,
                    helperText: errorMessage,
                }}
                name={name}
                type="text"
                labelText={getTranslated(i18n, translatedLabel)}
            />
        ),
    };
};
