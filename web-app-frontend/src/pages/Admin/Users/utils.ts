import { UserAdminSummaryDto } from 'api';
import { formatName, naiveTextCompare } from 'utils/util';
import { UserFiltersData } from './UserFilters';

const matchesTextPhrase = (user: UserAdminSummaryDto, searchPhrase: string) =>
    searchPhrase === '' ||
    naiveTextCompare(user.email, searchPhrase) ||
    naiveTextCompare(formatName(user.name, user.surname), searchPhrase);

export const filterUsers = (
    users: UserAdminSummaryDto[],
    { role, minCreationYear, maxCreationYear }: UserFiltersData,
    searchPhrase: string
) => {
    return users
        .filter((user) => matchesTextPhrase(user, searchPhrase))
        .filter((user) => role === 'any' || user.roles.find((urole) => `${urole}` === `${role}`))
        .filter(
            (user) =>
                user.createdAt.getFullYear() >= minCreationYear &&
                user.createdAt.getFullYear() <= maxCreationYear
        );
};
