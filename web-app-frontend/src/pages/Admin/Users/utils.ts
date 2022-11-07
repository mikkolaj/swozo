import {
    CreatePolicyRequest,
    CreatePolicyRequestPolicyTypeEnum,
    CreateUserRequest,
    PolicyDto,
    PolicyDtoPolicyTypeEnum,
    UserAdminDetailsDto,
    UserAdminSummaryDto,
    UserAdminSummaryDtoRolesEnum,
} from 'api';
import { QueryClient } from 'react-query';
import { formatName, naiveTextCompare } from 'utils/util';
import { UserFiltersData } from './UserFilters';

export type PolicyFormValues = Record<string, number>;
const INITIAL_POLICY_VALUE = 0;

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
        queryClient.setQueryData(['users', 'details', `${user.id}`], user);
    });
};

export const toPolicyFormValues = (policies: PolicyDto[]): PolicyFormValues => {
    return {
        ...Object.fromEntries(
            Object.values(PolicyDtoPolicyTypeEnum).map((policyType) => [policyType, INITIAL_POLICY_VALUE])
        ),
        ...Object.fromEntries(policies.map(({ policyType, value }) => [policyType, value])),
    };
};

export const updateUserDetailsCache = (user: UserAdminDetailsDto, queryClient: QueryClient) => {
    queryClient.setQueryData(['users', 'details', `${user.id}`], user);
};

export const toPolicyUpdateRequest = (teacherId: number, policies: PolicyFormValues): CreatePolicyRequest[] =>
    Object.entries(policies).map(([policyType, value]) => ({
        policyType: policyType as CreatePolicyRequestPolicyTypeEnum,
        value,
        teacherId,
    }));
