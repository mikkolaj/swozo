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
    optionEquals?: (first: T, second: T) => boolean;
    customChipRenderer?: (chips: JSX.Element[]) => JSX.Element;
};

export function AutocompleteWithChips<T>({
    labelPath,
    name,
    options,
    chosenOptions,
    optionToString,
    setFieldValue,
    customChipRenderer,
    optionEquals = (a, b) => a === b,
    required = true,
}: Props<T>) {
    const optionsMap = useMemo<Record<string, T>>(
        () => Object.fromEntries(options.map((option) => [optionToString(option), option])),
        [options, optionToString]
    );
    const [key, setKey] = useState(0);

    const chips = chosenOptions.map((option, idx) => (
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
    ));

    return (
        <>
            <Autocomplete
                key={key}
                freeSolo
                disableClearable
                options={options
                    .filter(
                        (option) =>
                            !chosenOptions.find((comparedOption) => optionEquals(option, comparedOption))
                    )
                    .map(optionToString)}
                onChange={(_, val) => {
                    const selectedOption = optionsMap[val];
                    if (selectedOption && !chosenOptions.includes(selectedOption)) {
                        setFieldValue(name, [...chosenOptions, selectedOption]);
                        setKey((key) => key + 1);
                    }
                }}
                renderInput={({ InputProps, ...params }) => (
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
                )}
            />
            {customChipRenderer ? (
                customChipRenderer(chips)
            ) : (
                <Box
                    sx={{
                        ...stylesRow,
                        mt: 1,
                        ml: 2,
                        width: '50%',
                        flexWrap: 'wrap',
                    }}
                >
                    {chips}
                </Box>
            )}
        </>
    );
}
