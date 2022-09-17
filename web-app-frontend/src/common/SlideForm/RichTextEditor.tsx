import { Box, SxProps } from '@mui/material';
import { Theme } from '@mui/system';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

type Props = {
    value: string;
    name: string;
    setFieldValue: (name: string, value: string) => void;
    wrapperSx?: SxProps<Theme>;
};

export const RichTextEditor = ({ value, name, setFieldValue, wrapperSx }: Props) => {
    return (
        <Box sx={wrapperSx}>
            <ReactQuill
                theme="snow"
                value={value}
                onChange={(v) => {
                    setFieldValue(name, v);
                }}
            />
        </Box>
    );
};
