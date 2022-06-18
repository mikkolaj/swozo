import dayjs, { Dayjs } from 'dayjs';

export type CourseSummary = {
    id: number;
    name: string;
    teacherName: string;
    lastActivity: string;
    subject: string;
};

export const mockCourseSummaryList = [
    {
        id: 1,
        subject: 'Informatyka',
        name: 'Systemy operacyjne',
        teacherName: 'Marek Nowak',
        lastActivity: '25.03.2022',
    },
    {
        id: 2,
        subject: 'Socjologia',
        name: 'Pierwotniaki',
        teacherName: 'Grzegorz Rogus',
        lastActivity: '13.04.2022',
    },
    {
        id: 3,
        subject: 'Programowanie',
        name: 'Programowanie w języku Python',
        teacherName: 'Zbigniew Kowalski',
        lastActivity: '05.04.2022',
    },
];

export type LinkInfo = {
    url: string;
    serviceName: string;
    connectionInstruction: string;
    connectionInfo: string;
};

export type InstructionContent = {
    header?: string;
    body: string;
};

export type Activity = {
    id: number;
    name: string;
    date: string;
    links: LinkInfo[];
    instructions: InstructionContent[];
};

export type Course = CourseSummary & {
    activities: Activity[];
};

export const mockCourse: Course = {
    id: 1,
    name: 'Systemy operacyjne',
    teacherName: 'Marek Nowak',
    lastActivity: '25.03.2022',
    subject: 'Informatyka',
    activities: [
        {
            id: 1,
            name: 'Zajęcia 1',
            date: '24.03.2022',
            links: [
                {
                    url: 'https://www.facebook.com/',
                    serviceName: 'Facebook',
                    connectionInstruction:
                        '1. Wejdź w link\n2. Wpisz podany wyżej login i hasło w formularzu\n3. Otwórz zakładkę pliki',
                    connectionInfo: 'Login: student@123.swozo.com\nHasło: 123123',
                },
            ],
            instructions: [],
        },
        {
            id: 2,
            name: 'Zajęcia 2',
            date: '26.03.2022',
            links: [
                {
                    url: 'https://www.facebook.com/',
                    serviceName: 'Facebook',
                    connectionInstruction:
                        '1. Wejdź w link\n2. Wpisz podany wyżej login i hasło w formularzu\n3. Otwórz zakładkę pliki',
                    connectionInfo: 'Login: student@123.swozo.com\nHasło: 123123',
                },
                {
                    url: 'https://www.facebook.com/',
                    serviceName: 'Facebook',
                    connectionInstruction:
                        '1. Wejdź w link\n2. Wpisz podany wyżej login i hasło w formularzu\n3. Otwórz zakładkę pliki',
                    connectionInfo: 'Login: student@123.swozo.com\nHasło: 123123',
                },
            ],
            instructions: [
                {
                    header: 'Informacje ogólne',
                    body: 'Na zajeciach potrzebny bedzie mikrofon i kamera. Warto mieć mocny komputer itp. Nulla facilisi. Proin id diam dictum neque mattis consequat. Morbi viverra egestas tincidunt. Donec viverra malesuada ipsum ut fermentum. Aliquam tempus risus vulputate, dignissim arcu id, eleifend libero. Nullam a iaculis eros. Aliquam erat volutpat. Sed blandit eu metus vitae interdum. Integer at mi erat. Etiam varius interdum egestas. Aliquam vel metus vel erat mollis elementum. Etiam aliquet justo sit amet ipsum viverra auctor. Aliquam placerat turpis eu ligula consequat, sed varius massa cursus. Integer ullamcorper mauris ac fermentum auctor. Praesent pulvinar nunc quis ullamcorper molestie. ',
                },
                {
                    header: 'Przebieg zajęć z Jupyterem',
                    body: 'Otworzycie notebook i rozwiążecie tyle ile wam się uda, za tydzień będziemy kontynuuować.',
                },
            ],
        },
        {
            id: 3,
            name: 'Zajęcia 3',
            date: '28.03.2022',
            links: [],
            instructions: [],
        },
    ],
};

export type ModuleSummary = {
    id: number;
    name: string;
    creatorName: string;
    creationDate: string;
    subject: string;
    usedInActivitiesCount: number;
};

export const mockModuleSummaryList: ModuleSummary[] = [
    {
        id: 1,
        name: 'Sockety',
        subject: 'Informatyka',
        creatorName: 'Marek Nowak',
        creationDate: '11.05.2022',
        usedInActivitiesCount: 10,
    },
    {
        id: 2,
        name: 'Funkcje w Pythonie',
        subject: 'Programowanie',
        creatorName: 'Zbigniew Kowalski',
        creationDate: '15.05.2022',
        usedInActivitiesCount: 12,
    },
    {
        id: 3,
        name: 'Klasy w Pythonie',
        subject: 'Programowanie',
        creatorName: 'Zbigniew Kowalski',
        creationDate: '18.05.2022',
        usedInActivitiesCount: 0,
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
