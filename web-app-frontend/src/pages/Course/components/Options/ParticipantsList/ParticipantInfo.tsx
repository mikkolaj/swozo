import { Grid, Paper, Typography } from '@mui/material';
import { ParticipantDetailsDto } from 'api';
import { stylesRowCenteredVertical } from 'common/styles';
import { formatDate, formatName } from 'utils/util';

type Props = ParticipantDetailsDto;

export const ParticipantInfo = ({ participant, joinedAt }: Props) => {
    return (
        <Paper sx={{ p: 1, boxShadow: 2 }}>
            <Grid container>
                <Grid item xs={4}>
                    <Typography variant="body1">
                        {formatName(participant.name, participant.surname)}
                    </Typography>
                </Grid>
                <Grid sx={stylesRowCenteredVertical} item xs={4}>
                    <Typography variant="body1">{participant.email}</Typography>
                </Grid>
                <Grid item xs={1} sx={stylesRowCenteredVertical}>
                    <Typography variant="body1">{formatDate(joinedAt)}</Typography>
                </Grid>
            </Grid>
        </Paper>
    );
};
