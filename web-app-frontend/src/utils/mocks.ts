import { ServiceModuleSummaryDto } from 'api';

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
