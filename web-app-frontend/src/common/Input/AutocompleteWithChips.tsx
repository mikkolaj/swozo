import { Autocomplete, Box, Chip } from '@mui/material';
import { FORM_INPUT_WIDTH, stylesRow } from 'common/styles';
import { useMemo, useState } from 'react';
import { FormInputField } from './FormInputField';

type Props<T> = {
    labelPath: string;
    name: string;
    options: T[];
    chosenOptions: T[];
    optionToString: (option: T) => string;
    setFieldValue: (fieldName: string, value: T[]) => void;
    required?: boolean;
    fullWidthChips?: boolean;
    componentToRenderRightToFieldInput?: JSX.Element; // TODO refactor this xD
};

export function AutocompleteWithChips<T>({
    labelPath,
    name,
    options,
    chosenOptions,
    optionToString,
    setFieldValue,
    componentToRenderRightToFieldInput,
    required = true,
    fullWidthChips = false,
}: Props<T>) {
    const optionsMap = useMemo<Record<string, T>>(
        () => Object.fromEntries(options.map((option) => [optionToString(option), option])),
        [options, optionToString]
    );
    const [key, setKey] = useState(0);

    return (
        <>
            <Autocomplete
                key={key}
                freeSolo
                disableClearable
                options={options.filter((option) => !chosenOptions.includes(option)).map(optionToString)}
                onChange={(_, val) => {
                    const selectedOption = optionsMap[val];
                    if (selectedOption && !chosenOptions.includes(selectedOption)) {
                        setFieldValue(name, [...chosenOptions, selectedOption]);
                        setKey((key) => key + 1);
                    }
                }}
                renderInput={({ InputProps, ...params }) => (
                    <Box sx={{ ...stylesRow }}>
                        <FormInputField
                            name={'_' + name}
                            i18nLabel={labelPath}
                            textFieldProps={{
                                required,
                                sx: { width: FORM_INPUT_WIDTH },
                                InputProps: {
                                    ...InputProps,
                                    type: 'search',
                                },
                                ...params,
                            }}
                        />
                        {componentToRenderRightToFieldInput}
                    </Box>
                )}
            />
            <Box
                sx={{
                    ...stylesRow,
                    mt: 1,
                    ml: 2,
                    width: fullWidthChips ? '100%' : '50%',
                    flexWrap: 'wrap',
                }}
            >
                {chosenOptions.map((option, idx) => (
                    <Chip
                        key={idx}
                        sx={{ mr: 1 }}
                        label={optionToString(option)}
                        variant="outlined"
                        onDelete={() =>
                            setFieldValue(
                                name,
                                chosenOptions.filter((chosenOption) => chosenOption !== option)
                            )
                        }
                    />
                ))}
            </Box>
        </>
    );
}
