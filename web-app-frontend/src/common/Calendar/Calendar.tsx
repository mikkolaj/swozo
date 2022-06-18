import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { Button, Card, Grid, Paper, Typography } from '@mui/material';
import dayjs from 'dayjs';
import _ from 'lodash';
import { useState } from 'react';
import { mockCalendarActivities } from 'utils/mocks';
import { CalendarDay } from './components/CalendarDay';
import { buildWeeks, DAYS_IN_WEEK, getWeeksInMonthCount } from './utils';

export const Calendar = () => {
    const [displayedDate, setDisplayedDate] = useState(dayjs());
    const [daysInMonth, setDaysInMonth] = useState(buildWeeks(displayedDate));
    const [activities] = useState(mockCalendarActivities);

    return (
        <Card sx={{ p: 2 }}>
            <Grid container>
                <Grid
                    item
                    xs={12}
                    sx={{
                        display: 'flex',
                        alignContent: 'center',
                        flexDirection: 'row',
                        justifyItems: 'space-between',
                        mb: 1,
                    }}
                >
                    <Button
                        startIcon={<ArrowBackIcon />}
                        onClick={() => {
                            const nextDate = displayedDate.subtract(1, 'month');
                            setDaysInMonth(buildWeeks(nextDate));
                            setDisplayedDate(dayjs().isSame(nextDate, 'month') ? dayjs() : nextDate);
                        }}
                    />
                    <Typography
                        sx={{ width: '100%', textAlign: 'center', textTransform: 'capitalize' }}
                        variant="h6"
                    >
                        {displayedDate.format('MMMM YYYY')}
                    </Typography>
                    <Button
                        startIcon={<ArrowForwardIcon />}
                        onClick={() => {
                            const nextDate = displayedDate.add(1, 'month');
                            setDaysInMonth(buildWeeks(nextDate));
                            setDisplayedDate(dayjs().isSame(nextDate, 'month') ? dayjs() : nextDate);
                        }}
                    />
                </Grid>
                <Grid
                    item
                    xs={12}
                    sx={{
                        display: 'flex',
                        flexDirection: 'row',
                        marginBottom: '5px',
                    }}
                    justifyContent="space-between"
                    alignContent="center"
                >
                    {_.range(DAYS_IN_WEEK).map((day) => (
                        <Paper
                            sx={{ boxShadow: 2, width: '100%', textAlign: 'center', margin: '1px' }}
                            key={day}
                        >
                            {_.capitalize(
                                dayjs()
                                    .day((day + 1) % DAYS_IN_WEEK)
                                    .format('dd')
                            ) + '.'}
                        </Paper>
                    ))}
                </Grid>
                {_.range(getWeeksInMonthCount(displayedDate)).map((week) => (
                    <Grid
                        item
                        key={week}
                        xs={12}
                        sx={{ display: 'flex', alignContent: 'center', flexDirection: 'row' }}
                    >
                        {_.range(DAYS_IN_WEEK).map((day) => (
                            <CalendarDay
                                key={day}
                                displayedDate={displayedDate}
                                day={daysInMonth[week * DAYS_IN_WEEK + day]}
                                activities={activities}
                            />
                        ))}
                    </Grid>
                ))}
            </Grid>
        </Card>
    );
};
