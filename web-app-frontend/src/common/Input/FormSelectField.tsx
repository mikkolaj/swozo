import { FORM_INPUT_WIDTH } from 'common/styles';
import { ComponentProps, PropsWithChildren } from 'react';
import { FormInputField } from './FormInputField';

export const FormSelectField = ({
    children,
    ...props
}: PropsWithChildren<ComponentProps<typeof FormInputField>>) => {
    const { wrapperSx, textFieldProps, ...sfifProps } = props;

    return (
        <FormInputField
            wrapperSx={{ width: FORM_INPUT_WIDTH, ...wrapperSx }}
            textFieldProps={{ ...textFieldProps, select: true, fullWidth: true }}
            {...sfifProps}
        >
            {children}
        </FormInputField>
    );
};
