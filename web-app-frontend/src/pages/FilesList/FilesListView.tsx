/* eslint-disable react/jsx-key */
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import DownloadIcon from '@mui/icons-material/Download';
import FavoriteIcon from '@mui/icons-material/Favorite';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import { Box, Container, Grid, IconButton, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesRow, stylesRowCenteredVertical } from 'common/styles';
import { useDownload } from 'hooks/query/useDownload';
import { useMeQuery } from 'hooks/query/useMeQuery';
import { useUnsetFileAsFavourite } from 'hooks/query/useUnsetFileAsFavourite';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { formatDate } from 'utils/util';
import { opposite, SortDirection, sorted, SortKey, withSortDirection } from './utils';

export const FilesListView = () => {
    const { t } = useTranslation();
    const { me } = useMeQuery();
    const { pushApiError, removeApiError, isApiErrorSet } = useApiErrorHandling({});
    const [sortDirection, setSortDirection] = useState<SortDirection>('DESC');
    const [sortKey, setSortKey] = useState<SortKey>('createdAt');
    const sortedFiles = useMemo(() => sorted(me?.favouriteFiles, sortKey), [me, sortKey]);

    const { download } = useDownload({
        fetcher: (file) =>
            getApis().userApi.getFavouriteFileDownloadRequest({
                remoteFileId: file.id,
            }),
        onError: pushApiError,
        deps: [me],
    });

    const { unsetFileAsFavouriteMutation } = useUnsetFileAsFavourite();

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
                            itemKeyExtractor={({ file }) => file.id}
                            itemWraperSxProvider={(idx) => {
                                if (idx === 3) {
                                    return {
                                        ...stylesRow,
                                        margin: 'auto',
                                    };
                                }
                            }}
                            itemRenderer={({ file, activitySummaryDto }) => [
                                <>
                                    <InsertDriveFileIcon sx={{ height: '80%' }} />
                                    <Typography variant="body1">{file.name}</Typography>
                                </>,
                                <Typography variant="body1">{activitySummaryDto.courseName}</Typography>,
                                <Typography variant="body1">{formatDate(file.createdAt)}</Typography>,
                                <Box sx={{ ...stylesRow, ml: 'auto' }}>
                                    <IconButton color="primary" onClick={() => download(file)}>
                                        <DownloadIcon />
                                    </IconButton>
                                    <IconButton
                                        color="primary"
                                        onClick={() => unsetFileAsFavouriteMutation.mutate(file)}
                                    >
                                        <FavoriteIcon />
                                    </IconButton>
                                </Box>,
                            ]}
                        />
                    }
                />
            </Container>
        </PageContainer>
    );
};
