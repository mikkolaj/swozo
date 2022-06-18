import SchoolIcon from '@mui/icons-material/School';
import { Box, Grid, Paper, Popover, Typography } from '@mui/material';
import { blue, blueGrey } from '@mui/material/colors';
import { Dayjs } from 'dayjs';
import { useState } from 'react';
import { CalendarActivity } from 'utils/mocks';
import { NO_SUCH_DAY_IN_MONTH } from '../utils';

type Props = {
    displayedDate: Dayjs;
    day: number;
    activities: CalendarActivity[];
};

export const CalendarDay = ({ displayedDate, day, activities }: Props) => {
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);

    if (day === NO_SUCH_DAY_IN_MONTH) {
        return (
            <Paper
                sx={{
                    boxShadow: 2,
                    margin: '1px',
                    height: '70px',
                    width: '100%',
                    textAlign: 'center',
                    background: blueGrey[50],
                }}
            />
        );
    }

    const thisDate = displayedDate.date(day);
    const thisActivities = activities.filter(({ at }) => at.isSame(thisDate, 'day'));

    return (
        <Paper
            sx={{
                boxShadow: 3,
                margin: '1px',
                height: '70px',
                width: '100%',
                background: thisDate.isToday() ? blue[200] : 'inherit',
            }}
        >
            <Grid container>
                <Grid
                    item
                    xs={12}
                    sx={{
                        textAlign: 'center',
                        display: 'flex',
                        justifyItems: 'flex-start',
                        alignContent: 'flex-start',
                    }}
                >
                    <Typography
                        variant="body1"
                        component="div"
                        sx={{ marginLeft: '6px', marginTop: '2px', fontSize: '18px', textAlign: 'center' }}
                    >
                        {day}
                    </Typography>
                </Grid>
                {thisActivities.length > 0 && (
                    <Grid
                        item
                        xs={12}
                        sx={{
                            display: 'flex',
                            mt: '12px',
                            mr: '6px',
                            flexDirection: 'row',
                            justifyContent: 'flex-end',
                            alignItems: 'flex-end',
                        }}
                    >
                        <Box
                            onMouseEnter={(e) => setAnchorEl(e.currentTarget)}
                            onMouseLeave={() => setAnchorEl(null)}
                        >
                            <SchoolIcon />
                        </Box>
                    </Grid>
                )}
            </Grid>
            <Popover
                sx={{
                    pointerEvents: 'none',
                }}
                open={!!anchorEl}
                anchorEl={anchorEl}
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left',
                }}
                transformOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                }}
                onClose={() => setAnchorEl(null)}
                disableRestoreFocus
            >
                {thisActivities.map((activity, idx) => (
                    <Typography key={idx} sx={{ p: 1 }}>
                        {activity.description}
                    </Typography>
                ))}
            </Popover>
        </Paper>
    );
};
