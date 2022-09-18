import { Dayjs } from 'dayjs';

export const NO_SUCH_DAY_IN_MONTH = -1;

export const DAYS_IN_WEEK = 7;

// start numbering with monday instead of sunday
const normalizeDayOfWeek = (day: number): number => (day - 1 + DAYS_IN_WEEK) % DAYS_IN_WEEK;

export const getWeeksInMonthCount = (date: Dayjs): number => {
    const daysInMonthWithDaysSplitByMonth =
        normalizeDayOfWeek(date.startOf('month').day()) + date.daysInMonth();
    return Math.ceil(daysInMonthWithDaysSplitByMonth / DAYS_IN_WEEK);
};

export const buildWeeks = (date: Dayjs) => {
    const firstDay = normalizeDayOfWeek(date.startOf('month').day());
    const numberOfDays = date.daysInMonth();

    const dayArr = new Array(getWeeksInMonthCount(date) * DAYS_IN_WEEK).fill(NO_SUCH_DAY_IN_MONTH);

    for (let i = 0; i < numberOfDays; i++) {
        dayArr[firstDay + i] = i + 1;
    }

    return dayArr;
};
