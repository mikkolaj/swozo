import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import { FORM_INPUT_WIDTH } from 'common/styles';
import { Dayjs } from 'dayjs';
import { SlideFormInputField } from './SlideFormInputField';

type Props = {
    label: string;
    name: string;
    value: Dayjs;
    setFieldValue: (name: string, val: Dayjs) => void;
};

export const SlideFormTimePicker = ({ label, name, value, setFieldValue }: Props) => {
    return (
        <TimePicker
            label={label}
            value={value}
            ampm={false}
            onChange={(v) => {
                if (v && v.isValid()) setFieldValue(name, v);
            }}
            renderInput={({ name: _name, ...params }) => (
                <SlideFormInputField
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
