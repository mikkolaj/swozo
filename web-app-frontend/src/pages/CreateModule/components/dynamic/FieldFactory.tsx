import { Box } from '@mui/material';
import { ParameterDescription, ParameterDescriptionTypeEnum } from 'api';
import { FileInputButton } from 'common/Input/FileInputButton';
import { FormInputField } from 'common/Input/FormInputField';
import { stylesRowCenteredVertical } from 'common/styles';
import { i18n } from 'i18next';
import { getTranslated } from 'utils/util';
import { FieldUtils } from './utils';

type FieldProvider = (param: ParameterDescription, i18n: i18n, fieldUtils: FieldUtils) => JSX.Element;

type FieldFactory = Record<ParameterDescriptionTypeEnum, FieldProvider>;

export const fieldFactory = (): FieldFactory => {
    return {
        FILE: (
            { name, required, translatedLabel, clientValidationHelpers }: ParameterDescription,
            i18n,
            { setFieldValue, setAssociatedValue }
        ) => (
            <Box sx={stylesRowCenteredVertical}>
                <FormInputField
                    name={name}
                    wrapperSx={{ mt: 0 }}
                    type="text"
                    labelText={getTranslated(i18n, translatedLabel)}
                    textFieldProps={{ inputProps: { readOnly: true }, required }}
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
        TEXT: ({ name, required, translatedLabel }: ParameterDescription, i18n, { setFieldValue }) => (
            <FormInputField
                wrapperSx={{ mt: 0, width: '50%' }}
                textFieldProps={{
                    fullWidth: true,
                    required,
                    onChange: (e) => {
                        setFieldValue(e.target.value, ParameterDescriptionTypeEnum.Text);
                    },
                }}
                name={name}
                type="text"
                labelText={getTranslated(i18n, translatedLabel)}
            />
        ),
    };
};
