import { Box } from '@mui/material';
import { ParameterDescription, ParameterDescriptionTypeEnum } from 'api';
import { FileInputButton } from 'common/Input/FileInputButton';
import { FormInputField } from 'common/Input/FormInputField';
import { stylesRowCenteredVertical } from 'common/styles';

export type FieldUtils = {
    setValue: (value: unknown) => void;
};

type FieldProvider = (param: ParameterDescription, fieldUtils: FieldUtils) => JSX.Element;

type FieldFactory = Record<ParameterDescriptionTypeEnum, FieldProvider>;

export const getInitialValue = (type: ParameterDescriptionTypeEnum): unknown => {
    switch (type) {
        case ParameterDescriptionTypeEnum.File:
        case ParameterDescriptionTypeEnum.Text:
        default:
            return '';
    }
};

export const fieldFactory = (): FieldFactory => {
    return {
        FILE: ({ name }: ParameterDescription, { setValue }) => (
            <Box sx={stylesRowCenteredVertical}>
                <FormInputField
                    name={name}
                    wrapperSx={{ mt: 0 }}
                    type="text"
                    labelText="Startowy notebook"
                    textFieldProps={{ inputProps: { readOnly: true } }}
                />
                <FileInputButton
                    text="Wybierz plik"
                    onFilesSelected={(files) => {
                        console.log(files);
                        setValue(files[0]?.name ?? '');
                    }}
                />
            </Box>
        ),
        TEXT: (param) => <>{param.name} TODO</>,
    };
};
