import { Box, Card, CardContent, CircularProgress, Divider, SxProps, Theme, Typography } from '@mui/material';
import { stylesRowFullyCentered } from 'common/styles';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { UploadState } from 'services/features/files/fileSlice';
import { useAppSelector } from 'services/store';

type Props = {
    cardSx?: SxProps<Theme>;
};

export const FileProgressModal = ({ cardSx }: Props) => {
    const uploadState = useAppSelector((state) => state.files.uploads);
    const downloads = useAppSelector((state) => state.files.downloads);
    const [filesBeingUploaded, setFilesBeingUploaded] = useState<UploadState[]>([]);
    const { t } = useTranslation();

    useEffect(() => {
        setFilesBeingUploaded(Object.values(uploadState).filter(({ isUploading }) => isUploading));
    }, [uploadState]);

    if (filesBeingUploaded.length === 0) return <></>;

    return (
        <Card
            sx={{
                position: 'fixed',
                right: '40px',
                bottom: '30px',
                borderRadius: 5,
                border: 'none',
                boxShadow: 3,
                width: '200px',
            }}
        >
            <CardContent
                sx={{
                    minHeight: '70px',
                    maxHeight: '100px',
                    p: 0,
                    overflowY: 'scroll',
                    '::-webkit-scrollbar': {
                        display: 'none',
                    },
                    ...cardSx,
                }}
            >
                <Box sx={{ ...stylesRowFullyCentered, p: 1, justifyContent: 'center' }}>
                    <Typography sx={{ mr: 2 }}>{t('commonModals.upOrDownloading')}</Typography>
                    <CircularProgress size={20} thickness={10} sx={{ animationDuration: '1500ms' }} />
                </Box>
                <Divider sx={{ mt: -0.5 }} />
                <Box sx={{ m: 1, mb: 0.1 }}>
                    {[...downloads, ...filesBeingUploaded].slice(0, 2).map(({ filename, startTimestamp }) => (
                        <Typography
                            sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }}
                            key={`${filename}_${startTimestamp}`}
                        >
                            {filename}
                        </Typography>
                    ))}
                </Box>
            </CardContent>
        </Card>
    );
};
