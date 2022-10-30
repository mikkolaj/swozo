/* eslint-disable react/jsx-key */
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import DeleteIcon from '@mui/icons-material/Delete';
import DownloadIcon from '@mui/icons-material/Download';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import ShareIcon from '@mui/icons-material/Share';
import { Container, Grid, IconButton, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesRow, stylesRowCenteredVertical } from 'common/styles';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { mockFiles } from 'utils/mocks';
import { formatDate } from 'utils/util';
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
                    <PageHeaderText text={t('myFiles.header')} />
                </Grid>
            }
        >
            <Container>
                <StackedList
                    header={
                        <StackedListHeader
                            proportions={[5, 4, 3]}
                            itemWrapperSxProvider={(idx) => {
                                if (idx === 2) {
                                    return {
                                        ...stylesRowCenteredVertical,
                                        ml: -1,
                                    };
                                }
                            }}
                            items={[
                                <Typography variant="body1" color="GrayText">
                                    {t('myFiles.fileName')}
                                </Typography>,
                                <Typography variant="body1" color="GrayText">
                                    {t('myFiles.courseName')}
                                </Typography>,
                                <>
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
                                            {sortDirection === 'DESC' ? (
                                                <ArrowDownwardIcon />
                                            ) : (
                                                <ArrowUpwardIcon />
                                            )}
                                        </IconButton>
                                    )}
                                </>,
                            ]}
                        />
                    }
                    content={
                        <StackedListContent
                            proportions={[5, 4, 1, 1]}
                            items={withSortDirection(sortedFiles, sortDirection)}
                            itemKeyExtractor={(file) => file.id}
                            itemWraperSxProvider={(idx) => {
                                if (idx === 3) {
                                    return {
                                        ...stylesRow,
                                        margin: 'auto',
                                    };
                                }
                            }}
                            itemRenderer={(file) => [
                                <>
                                    <InsertDriveFileIcon sx={{ height: '80%' }} />
                                    <Typography variant="body1">{file.name}</Typography>
                                </>,
                                <Typography variant="body1">{file.courseName}</Typography>,
                                <Typography variant="body1">
                                    {formatDate(file.createdAt.toDate())}
                                </Typography>,
                                <>
                                    <IconButton color="primary">
                                        <DownloadIcon />
                                    </IconButton>
                                    <IconButton color="primary">
                                        <ShareIcon />
                                    </IconButton>
                                    <IconButton color="primary">
                                        <DeleteIcon />
                                    </IconButton>
                                </>,
                            ]}
                        />
                    }
                />
            </Container>
        </PageContainer>
    );
};
