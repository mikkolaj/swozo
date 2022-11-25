import { InputField } from 'common/Input/InputField';

type Props = {
    name: string;
    type: string;
    i18nLabel: string;
    autofocus?: boolean;
};

export const StyledInputField = ({ name, type, i18nLabel, autofocus = false }: Props) => {
    return (
        <InputField
            name={name}
            type={type}
            textFieldProps={{
                required: true,
                fullWidth: true,
                autoFocus: autofocus,
                variant: 'outlined',
            }}
            wrapperSx={{
                mt: 2,
                width: '100%',
            }}
            i18nLabel={i18nLabel}
        />
    );
};
