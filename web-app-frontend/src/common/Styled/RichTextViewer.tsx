import { Box, SxProps, Typography } from '@mui/material';
import { Theme } from '@mui/system';
import 'react-quill/dist/quill.snow.css';

type Props = {
    sanitizedHtmlData: string;
    wrapperSx?: SxProps<Theme>;
};

export const RichTextViewer = ({ sanitizedHtmlData, wrapperSx }: Props) => {
    return (
        <Box sx={wrapperSx}>
            <Typography variant="body1">
                <span className="ql-editor" dangerouslySetInnerHTML={{ __html: sanitizedHtmlData }} />
            </Typography>
        </Box>
    );
};
