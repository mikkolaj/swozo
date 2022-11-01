import {
    CreateUserRequest,
    UserAdminDetailsDto,
    UserAdminSummaryDto,
    UserAdminSummaryDtoRolesEnum,
} from 'api';
import { QueryClient } from 'react-query';
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

export const userDetailsToSummary = (userAdminDetails: UserAdminDetailsDto): UserAdminSummaryDto => ({
    ...userAdminDetails,
    roles: userAdminDetails.roles as unknown as UserAdminSummaryDtoRolesEnum[],
});

export const initialUserValues = (): CreateUserRequest => ({
    email: '',
    name: '',
    surname: '',
    roles: [],
});

export const updateUserCacheAfterCreation = (users: UserAdminDetailsDto[], queryClient: QueryClient) => {
    queryClient.setQueryData(['users'], (previousUsers: UserAdminSummaryDto[] = []) => [
        ...users.map(userDetailsToSummary),
        ...previousUsers,
    ]);
    users.forEach((user) => {
        queryClient.setQueryData(['users', `${user.id}`], user);
    });
};
