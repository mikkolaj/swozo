import { Autocomplete, Box, Chip } from '@mui/material';
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
                    const opt = options
                        .filter((option) => !choosenOptions.includes(option))
                        .find((option) => optionToString(option) === val);

                    if (opt !== undefined) {
                        setFieldValue(name, [...choosenOptions, opt]);
                    }
                }}
                renderInput={({ InputProps, ...params }) => (
                    <SlideFormInputField
                        name={'_' + name}
                        labelPath={labelPath}
                        textFieldProps={{
                            sx: { width: '230px' },
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
                    mt: 1,
                    ml: 2,
                    width: '50%',
                    display: 'flex',
                    flexDirection: 'row',
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
