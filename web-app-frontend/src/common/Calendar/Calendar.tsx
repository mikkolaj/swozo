import { Box, Card, Grid, Typography } from '@mui/material';
import moment from 'moment';
import { WEEKDAYS } from './utils';

export const Calendar = () => {
    console.log(moment().startOf('month').day());

    return (
        <Card sx={{ p: 2 }}>
            <Grid container>
                <Typography variant="h6">Marzec 2022</Typography>
                <Grid
                    item
                    xs={12}
                    sx={{
                        display: 'flex',
                        flexDirection: 'row',
                        borderTop: '1px solid black',
                        borderBottom: '1px solid black',
                    }}
                    justifyContent="space-between"
                    alignContent="center"
                >
                    {WEEKDAYS.map((day, idx) => (
                        <Box
                            sx={{
                                display: 'inline-block',
                                p: 2,
                                borderRight: '1px solid black',
                                borderLeft: idx === 0 ? '1px solid black' : 'none',
                            }}
                            key={day}
                        >
                            {day}
                        </Box>
                    ))}
                </Grid>
                <Grid item xs={12}>
                    <Box width="100%" height={350} sx={{ background: '#eee' }} />
                </Grid>
            </Grid>
        </Card>
    );
};
