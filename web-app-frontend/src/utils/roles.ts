import { AuthData, AuthDataRolesEnum } from 'api';

export type AuthRequirement = {
    loggedIn: boolean;
    roles: AuthDataRolesEnum[];
};

export const NOT_LOGGED_IN: AuthRequirement = {
    loggedIn: false,
    roles: [],
};

export const ANY_LOGGED_IN: AuthRequirement = {
    loggedIn: true,
    roles: [...(Object.values(AuthDataRolesEnum) as AuthDataRolesEnum[])],
};

export const withRole = (...roles: AuthDataRolesEnum[]): AuthRequirement => {
    return {
        loggedIn: true,
        roles,
    };
};

export const hasRole = (authData: AuthData, ...roles: AuthDataRolesEnum[]): boolean => {
    return roles.find((role) => authData.roles.includes(role)) !== undefined;
};
