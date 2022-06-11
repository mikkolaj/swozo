import { AuthData, AuthDataRolesEnum } from 'api';
import { useAppSelector } from 'services/store';

export const STUDENT = AuthDataRolesEnum.Student;
export const TEACHER = AuthDataRolesEnum.Teacher;
export const TECHNICAL_TEACHER = AuthDataRolesEnum.TechnicalTeacher;
export const ADMIN = AuthDataRolesEnum.Admin;

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

export const hasRole = (authData: AuthData | null, ...roles: AuthDataRolesEnum[]): boolean => {
    // TODO handling admin
    if (!authData) return false;
    return roles.find((role) => authData.roles.includes(role)) !== undefined;
};

export const WithRole = ({ children, roles }: React.PropsWithChildren<{ roles: AuthDataRolesEnum[] }>) => {
    const auth = useAppSelector((state) => state.auth.authData);
    return <>{hasRole(auth, ...roles) && children}</>;
};

export const WithPreference = ({ children, role }: React.PropsWithChildren<{ role: AuthDataRolesEnum }>) => {
    const auth = useAppSelector((state) => state.auth);
    return <>{hasRole(auth.authData, role) && auth.rolePreference === role && children}</>;
};
