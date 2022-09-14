import { FORM_INPUT_WIDTH } from 'common/styles';
import { ComponentProps, PropsWithChildren } from 'react';
import { SlideFormInputField } from './SlideFormInputField';

export const SlideFormSelectField = ({
    children,
    ...props
}: PropsWithChildren<ComponentProps<typeof SlideFormInputField>>) => {
    const { wrapperSx, textFieldProps, ...sfifProps } = props;

    return (
        <SlideFormInputField
            wrapperSx={{ ...wrapperSx, width: FORM_INPUT_WIDTH }}
            textFieldProps={{ ...textFieldProps, select: true, fullWidth: true }}
            {...sfifProps}
        >
            {children}
        </SlideFormInputField>
    );
};
