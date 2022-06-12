import { Dayjs } from 'dayjs';

export const NO_SUCH_DAY_IN_MONTH = -1;

export const DAYS_IN_WEEK = 7;

export const getWeeksInMonthCount = (date: Dayjs): number => {
    return Math.ceil(
        (((date.startOf('month').day() - 1 + DAYS_IN_WEEK) % DAYS_IN_WEEK) + date.daysInMonth()) /
            DAYS_IN_WEEK
    );
};

export const buildWeeks = (date: Dayjs) => {
    const firstDay = (date.startOf('month').day() - 1 + DAYS_IN_WEEK) % DAYS_IN_WEEK;
    const numberOfDays = date.daysInMonth();

    const dayArr = new Array(getWeeksInMonthCount(date) * DAYS_IN_WEEK).fill(NO_SUCH_DAY_IN_MONTH);

    for (let i = 0; i < numberOfDays; i++) {
        dayArr[firstDay + i] = i + 1;
    }

    return dayArr;
};
