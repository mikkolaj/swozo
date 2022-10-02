import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import { FORM_INPUT_WIDTH } from 'common/styles';
import { Dayjs } from 'dayjs';
import { FormInputField } from './FormInputField';

type Props = {
    label: string;
    name: string;
    value: Dayjs;
    setFieldValue: (name: string, val: Dayjs) => void;
};

export const FormTimePicker = ({ label, name, value, setFieldValue }: Props) => {
    return (
        <TimePicker
            label={label}
            value={value}
            ampm={false}
            onChange={(v) => {
                if (v && v.isValid()) setFieldValue(name, v);
            }}
            renderInput={({ name: _name, ...params }) => (
                <FormInputField
                    name={name}
                    textFieldProps={{
                        sx: { width: FORM_INPUT_WIDTH },
                        ...params,
                    }}
                />
            )}
        />
    );
};
