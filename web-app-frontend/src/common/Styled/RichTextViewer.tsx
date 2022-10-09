import { Box, SxProps, Typography } from '@mui/material';
import { Theme } from '@mui/system';
import * as DOMPurify from 'dompurify';
import { useMemo } from 'react';
import 'react-quill/dist/quill.snow.css';

type Props = {
    untrustedPossiblyDangerousHtml: string;
    wrapperSx?: SxProps<Theme>;
};

export const RichTextViewer = ({ untrustedPossiblyDangerousHtml, wrapperSx }: Props) => {
    const sanitizedHtml = useMemo(
        () => DOMPurify.sanitize(untrustedPossiblyDangerousHtml),
        [untrustedPossiblyDangerousHtml]
    );

    return (
        <Box sx={wrapperSx}>
            <Typography variant="body1">
                <span className="ql-editor" dangerouslySetInnerHTML={{ __html: sanitizedHtml }} />
            </Typography>
        </Box>
    );
};
