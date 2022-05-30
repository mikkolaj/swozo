export type CourseSummary = {
    id: number;
    name: string;
    teacherName: string;
    lastActivity: string;
};

export const mockCourseSummaryList = [
    {
        id: 1,
        name: 'Systemy operacyjne',
        teacherName: 'Marek Nowak',
        lastActivity: '25.03.2022',
    },
    {
        id: 2,
        name: 'Pierwotniaki',
        teacherName: 'Grzegorz Rogus',
        lastActivity: '13.04.2022',
    },
    {
        id: 3,
        name: 'Programowanie w języku Python',
        teacherName: 'Zbigniew Kowalski',
        lastActivity: '05.04.2022',
    },
];

export type LinkInfo = {
    url: string;
    serviceName: string;
    serviceInfo: string;
};

export type Activity = {
    id: number;
    name: string;
    date: string;
    links: LinkInfo[];
    instructions: string[];
};

export type Course = CourseSummary & {
    activities: Activity[];
};

export const mockCourse: Course = {
    id: 1,
    name: 'Systemy operacyjne',
    teacherName: 'Marek Nowak',
    lastActivity: '25.03.2022',
    activities: [
        {
            id: 1,
            name: 'Zajęcia 1',
            date: '24.03.2022',
            links: [
                {
                    url: 'https://www.facebook.com/',
                    serviceName: 'Facebook',
                    serviceInfo:
                        'Facebook is an American online social media and social networking service owned by Meta Platforms. Founded in 2004 by Mark Zuckerberg with fellow Harvard College students and roommates',
                },
            ],
            instructions: ['just login'],
        },
        {
            id: 2,
            name: 'Zajęcia 2',
            date: '26.03.2022',
            links: [
                {
                    url: 'https://www.facebook.com/',
                    serviceName: 'Facebook',
                    serviceInfo:
                        'Facebook is an American online social media and social networking service owned by Meta Platforms. Founded in 2004 by Mark Zuckerberg with fellow Harvard College students and roommates',
                },
                {
                    url: 'https://www.facebook.com/',
                    serviceName: 'Facebook',
                    serviceInfo:
                        'Facebook is an American online social media and social networking service owned by Meta Platforms.',
                },
            ],
            instructions: ['just login'],
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
