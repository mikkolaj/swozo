import dayjs from 'dayjs';

export const DATE_FORMAT = 'DD.MM.YYYY';

export const TIME_FORMAT = 'HH:mm';

export const DATE_TIME_FORMAT = `${TIME_FORMAT} ${DATE_FORMAT}`;

export const formatDate = (date: Date): string => {
    return dayjs(date).format(DATE_FORMAT);
};

export const formatTime = (date: Date): string => {
    return dayjs(date).format(TIME_FORMAT);
};

export const formatDateTime = (date: Date): string => {
    // TODO
    return dayjs(date).format(DATE_TIME_FORMAT);
};
