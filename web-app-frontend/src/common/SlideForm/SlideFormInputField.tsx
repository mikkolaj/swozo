import { InputField } from 'common/Input/InputField';
import { ComponentProps } from 'react';

export const SlideFormInputField = ({
    wrapperSx,
    textFieldProps,
    children,
    ...props
}: ComponentProps<typeof InputField>) => {
    const { required, ...tfProps } = textFieldProps ?? { required: true };

    return (
        <InputField
            wrapperSx={{ mt: 2, ...wrapperSx }}
            textFieldProps={{ required: required ?? true, ...tfProps }}
            {...props}
        >
            {children}
        </InputField>
    );
};
