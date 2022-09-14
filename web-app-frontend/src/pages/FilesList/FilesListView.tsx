import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import DeleteIcon from '@mui/icons-material/Delete';
import DownloadIcon from '@mui/icons-material/Download';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import ShareIcon from '@mui/icons-material/Share';
import { Box, Container, Divider, Grid, IconButton, Paper, Stack, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { stylesRow, stylesRowCenteredVertical } from 'common/styles';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { mockFiles } from 'utils/mocks';
import { sorted, SortKey } from './utils';

export const FilesListView = () => {
    const { t } = useTranslation();
    const [files] = useState(mockFiles);
    const [sortIncreasing, setSortIncreasing] = useState(true);
    const [sortKey, setSortKey] = useState<SortKey>('createdAt');

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
                                            setSortIncreasing((sortIncreasing) => !sortIncreasing);
                                        } else {
                                            setSortKey('createdAt');
                                        }
                                    }}
                                >
                                    {sortIncreasing ? <ArrowDownwardIcon /> : <ArrowUpwardIcon />}
                                </IconButton>
                            )}
                        </Grid>
                    </Grid>

                    <Divider />

                    {/* TODO memoize this */}
                    {sorted(files, sortKey, sortIncreasing ? 'ASC' : 'DESC').map((file) => (
                        <Paper key={file.id} sx={{ p: 1, boxShadow: 2 }}>
                            <Grid container>
                                <Grid item xs={5} sx={stylesRowCenteredVertical}>
                                    <InsertDriveFileIcon sx={{ height: '80%' }} />
                                    <Typography variant="body1">{file.name}</Typography>
                                </Grid>
                                <Grid sx={stylesRowCenteredVertical} item xs={4}>
                                    <Typography variant="body1">{file.courseName}</Typography>
                                </Grid>
                                <Grid item xs={1} sx={stylesRowCenteredVertical}>
                                    <Typography variant="body1">
                                        {file.createdAt.format('DD.MM.YYYY')}
                                    </Typography>
                                </Grid>
                                <Grid
                                    item
                                    xs={1}
                                    sx={{
                                        ...stylesRow,
                                        margin: 'auto',
                                    }}
                                >
                                    <IconButton color="primary">
                                        <DownloadIcon />
                                    </IconButton>
                                    <IconButton color="primary">
                                        <ShareIcon />
                                    </IconButton>
                                    <IconButton color="primary">
                                        <DeleteIcon />
                                    </IconButton>
                                </Grid>
                            </Grid>
                        </Paper>
                    ))}
                </Stack>
                <Box sx={{ height: 1000 }} />
            </Container>
        </PageContainer>
    );
};
