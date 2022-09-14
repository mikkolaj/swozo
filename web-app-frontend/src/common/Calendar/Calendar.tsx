import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { Button, Card, Grid, Paper, Typography } from '@mui/material';
import { stylesRowCenteredVertical } from 'common/styles';
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
                        ...stylesRowCenteredVertical,
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
                        ...stylesRowCenteredVertical,
                        marginBottom: '5px',
                    }}
                >
                    {_.range(DAYS_IN_WEEK).map((day) => (
                        <Paper
                            sx={{
                                boxShadow: 2,
                                width: '100%',
                                textAlign: 'center',
                                margin: '1px',
                                textTransform: 'capitalize',
                            }}
                            key={day}
                        >
                            {dayjs()
                                .day((day + 1) % DAYS_IN_WEEK)
                                .format('dd') + '.'}
                        </Paper>
                    ))}
                </Grid>
                {_.range(getWeeksInMonthCount(displayedDate)).map((week) => (
                    <Grid item key={week} xs={12} sx={stylesRowCenteredVertical}>
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
