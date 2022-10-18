import { ServiceModuleDetailsDto } from 'api';
import dayjs, { Dayjs } from 'dayjs';

// should have different type but will do for now
export const mockGeneralModuleSummaryList: ServiceModuleDetailsDto[] = [
    {
        id: -1,
        name: 'Wideokonferencja',
        subject: 'Programowanie',
        creator: {
            name: 'Zbigniew',
            surname: 'Kowalski',
            email: 'zkowalski@gmail.com',
        },
        createdAt: new Date(),
        instruction: { untrustedPossiblyDangerousHtml: 'kamerka i mikrofon' },
        description: 'test',
        isPublic: true,
        serviceName: 'JUPYTER',
        usedInActivitesCount: 0,
    },
];

export const mockServices: string[] = ['Jupyter', 'Sozisel'];

export type CalendarActivity = {
    at: Dayjs;
    description: string;
};

export const mockCalendarActivities: CalendarActivity[] = [
    {
        at: dayjs().day(1),
        description: 'Zajęcia: Klasy w Pythonie o 14:40',
    },
    {
        at: dayjs().day(-3),
        description: 'Zajęcia: Funkcje w Pythonie o 14:40',
    },
];

export type FileSummary = {
    id: number;
    name: string;
    courseName: string;
    courseId: number;
    createdAt: Dayjs;
};

export const mockFiles: FileSummary[] = [
    {
        id: 0,
        name: 'zadanie1.ipynb',
        courseName: 'Programowanie w języku Python',
        courseId: 3,
        createdAt: dayjs().day(-10),
    },
    {
        id: 1,
        name: 'lab3.vhd',
        courseName: 'Systemy operacyjne',
        courseId: 1,
        createdAt: dayjs().day(-12),
    },
    {
        id: 2,
        name: 'zadanie3.ipynb',
        courseName: 'Programowanie w języku Python',
        courseId: 3,
        createdAt: dayjs().day(-3),
    },
];
