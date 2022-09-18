import DeleteIcon from '@mui/icons-material/Delete';
import DownloadIcon from '@mui/icons-material/Download';
import InsertDriveFileIcon from '@mui/icons-material/InsertDriveFile';
import ShareIcon from '@mui/icons-material/Share';
import { Grid, IconButton, Paper, Typography } from '@mui/material';
import { stylesRow, stylesRowCenteredVertical } from 'common/styles';
import { FileSummary } from 'utils/mocks';
import { formatDate } from 'utils/util';

export type Props = {
    file: FileSummary;
};

export const FileView = ({ file }: Props) => {
    return (
        <Paper sx={{ p: 1, boxShadow: 2 }}>
            <Grid container>
                <Grid item xs={5} sx={stylesRowCenteredVertical}>
                    <InsertDriveFileIcon sx={{ height: '80%' }} />
                    <Typography variant="body1">{file.name}</Typography>
                </Grid>
                <Grid sx={stylesRowCenteredVertical} item xs={4}>
                    <Typography variant="body1">{file.courseName}</Typography>
                </Grid>
                <Grid item xs={1} sx={stylesRowCenteredVertical}>
                    <Typography variant="body1">{formatDate(file.createdAt.toDate())}</Typography>
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
    );
};
