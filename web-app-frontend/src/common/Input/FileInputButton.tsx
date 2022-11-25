import UploadIcon from '@mui/icons-material/Upload';
import { Button } from '@mui/material';
import { ComponentProps } from 'react';

type Props = ComponentProps<typeof Button> & {
    text?: string;
    allowedExtensions?: string[];
    onFilesSelected?: (files: File[]) => void;
    onFileSelected?: (file: File) => void;
};

export const FileInputButton = ({
    text,
    sx,
    endIcon,
    variant,
    allowedExtensions,
    onFilesSelected,
    onFileSelected,
}: Props) => {
    return (
        <Button
            sx={{ ml: 2, height: 56, ...sx }}
            endIcon={endIcon ?? <UploadIcon />}
            variant={variant ?? 'contained'}
            component="label"
        >
            {text}
            <input
                type="file"
                hidden
                multiple={!!onFilesSelected}
                accept={allowedExtensions?.map((ext) => `.${ext}`).join(',')}
                onChange={({ target }) => {
                    if (target.files && target.files.length > 0) {
                        if (onFilesSelected) {
                            onFilesSelected([...target.files]);
                        } else if (onFileSelected) {
                            onFileSelected(target.files[0]);
                        }
                    }
                }}
            />
        </Button>
    );
};
