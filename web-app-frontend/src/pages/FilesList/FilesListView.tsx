import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import { Box, Container, Divider, Grid, IconButton, Stack, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { stylesRowCenteredVertical } from 'common/styles';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { mockFiles } from 'utils/mocks';
import { FileView } from './components/FileView';
import { opposite, SortDirection, sorted, SortKey, withSortDirection } from './utils';

export const FilesListView = () => {
    const { t } = useTranslation();
    const [files] = useState(mockFiles);
    const [sortDirection, setSortDirection] = useState<SortDirection>('DESC');
    const [sortKey, setSortKey] = useState<SortKey>('createdAt');
    const sortedFiles = useMemo(() => sorted(files, sortKey), [files, sortKey]);

    return (
        <PageContainer
            header={
                <Grid item xs={4}>
                    <Typography variant="h4" component="div">
                        {t('myFiles.header')}
                    </Typography>
                </Grid>
            }
        >
            <Container>
                <Stack spacing={2} px={2}>
                    <Grid container sx={{ mb: -2 }}>
                        <Grid item xs={5} sx={stylesRowCenteredVertical}>
                            <Typography variant="body1" color="GrayText">
                                {t('myFiles.fileName')}
                            </Typography>
                        </Grid>
                        <Grid sx={stylesRowCenteredVertical} item xs={4}>
                            <Typography variant="body1" color="GrayText">
                                {t('myFiles.courseName')}
                            </Typography>
                        </Grid>
                        <Grid item xs={3} sx={{ ...stylesRowCenteredVertical, ml: -1 }}>
                            <Typography variant="body1" color="GrayText">
                                {t('myFiles.creationDate')}
                            </Typography>
                            {sortKey === 'createdAt' && (
                                <IconButton
                                    onClick={() => {
                                        if (sortKey === 'createdAt') {
                                            setSortDirection((direction) => opposite(direction));
                                        } else {
                                            setSortKey('createdAt');
                                        }
                                    }}
                                >
                                    {sortDirection === 'DESC' ? <ArrowDownwardIcon /> : <ArrowUpwardIcon />}
                                </IconButton>
                            )}
                        </Grid>
                    </Grid>

                    <Divider />

                    {withSortDirection(sortedFiles, sortDirection).map((file) => (
                        <FileView key={file.id} file={file} />
                    ))}
                </Stack>
                <Box sx={{ height: 1000 }} />
            </Container>
        </PageContainer>
    );
};
