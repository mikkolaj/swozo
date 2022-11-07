import { ServiceModuleSummaryDto } from 'api';
import dayjs, { Dayjs } from 'dayjs';

// should have different type but will do for now
export const mockGeneralModuleSummaryList: ServiceModuleSummaryDto[] = [
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
        teacherInstruction: { untrustedPossiblyDangerousHtml: 't' },
        studentInstruction: { untrustedPossiblyDangerousHtml: 's' },
        description: 'test',
        serviceName: 'JUPYTER',
        usedInActivitiesCount: 0,
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
