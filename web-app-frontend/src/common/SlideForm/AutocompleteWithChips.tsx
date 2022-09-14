import { Autocomplete, Box, Chip } from '@mui/material';
import { FORM_INPUT_WIDTH, stylesRow } from 'common/styles';
import { SlideFormInputField } from './SlideFormInputField';

type Props<T> = {
    labelPath: string;
    name: string;
    options: T[];
    choosenOptions: T[];
    optionToString: (option: T) => string;
    setFieldValue: (fieldName: string, value: T[]) => void;
};

export function AutocompleteWithChips<T>({
    labelPath,
    name,
    options,
    choosenOptions,
    optionToString,
    setFieldValue,
}: Props<T>) {
    return (
        <>
            <Autocomplete
                freeSolo
                disableClearable
                options={options.filter((option) => !choosenOptions.includes(option)).map(optionToString)}
                onChange={(_, val) => {
                    // TODO optimize this
                    const newOption = options
                        .filter((option) => !choosenOptions.includes(option))
                        .find((option) => optionToString(option) === val);

                    if (newOption !== undefined) {
                        setFieldValue(name, [...choosenOptions, newOption]);
                    }
                }}
                renderInput={({ InputProps, ...params }) => (
                    <SlideFormInputField
                        name={'_' + name}
                        i18nLabel={labelPath}
                        textFieldProps={{
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
            <Box
                sx={{
                    ...stylesRow,
                    mt: 1,
                    ml: 2,
                    width: '50%',
                    flexWrap: 'wrap',
                }}
            >
                {choosenOptions.map((option, idx) => (
                    <Chip
                        key={idx}
                        sx={{ mr: 1 }}
                        label={optionToString(option)}
                        variant="outlined"
                        onDelete={() =>
                            setFieldValue(
                                name,
                                choosenOptions.filter((choosenOption) => choosenOption !== option)
                            )
                        }
                    />
                ))}
            </Box>
        </>
    );
}
