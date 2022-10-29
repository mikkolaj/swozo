import SearchIcon from '@mui/icons-material/Search';
import { InputAdornment, TextField } from '@mui/material';
import { ComponentProps } from 'react';

type Props = ComponentProps<typeof TextField> & {
    placeholder: string;
};

export const SearchBar = ({ placeholder, ...props }: Props) => {
    const { InputProps, ...rest } = props;
    return (
        <TextField
            variant="outlined"
            placeholder={placeholder}
            InputProps={{
                startAdornment: (
                    <InputAdornment position="start">
                        <SearchIcon />
                    </InputAdornment>
                ),
                ...InputProps,
            }}
            {...rest}
        />
    );
};
