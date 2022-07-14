import { AuthDetailsDto, AuthDetailsDtoRolesEnum } from 'api';
import { PageGuard } from 'common/PageGuard/PageGuard';
import { useAppSelector } from 'services/store';

export const STUDENT = AuthDetailsDtoRolesEnum.Student;
export const TEACHER = AuthDetailsDtoRolesEnum.Teacher;
export const TECHNICAL_TEACHER = AuthDetailsDtoRolesEnum.TechnicalTeacher;
export const ADMIN = AuthDetailsDtoRolesEnum.Admin;

export type AuthRequirement = {
    loggedIn: boolean;
    roles: AuthDetailsDtoRolesEnum[];
};

export const NOT_LOGGED_IN: AuthRequirement = {
    loggedIn: false,
    roles: [],
};

export const ANY_LOGGED_IN: AuthRequirement = {
    loggedIn: true,
    roles: [...(Object.values(AuthDetailsDtoRolesEnum) as AuthDetailsDtoRolesEnum[])],
};

export const withRole = (...roles: AuthDetailsDtoRolesEnum[]): AuthRequirement => {
    return {
        loggedIn: true,
        roles,
    };
};

export const hasRole = (authData: AuthDetailsDto | null, ...roles: AuthDetailsDtoRolesEnum[]): boolean => {
    // TODO handling admin
    if (!authData) return false;
    return roles.find((role) => authData.roles.includes(role)) !== undefined;
};

export const WithRole = ({
    children,
    roles,
}: React.PropsWithChildren<{ roles: AuthDetailsDtoRolesEnum[] }>) => {
    const auth = useAppSelector((state) => state.auth.authData);
    return <>{hasRole(auth, ...roles) && children}</>;
};

export const WithPreference = ({
    children,
    role,
}: React.PropsWithChildren<{ role: AuthDetailsDtoRolesEnum }>) => {
    const auth = useAppSelector((state) => state.auth);
    return <>{hasRole(auth.authData, role) && auth.rolePreference === role && children}</>;
};

export const guarded = (element: JSX.Element, authRequirement: AuthRequirement): JSX.Element => {
    return <PageGuard authRequirement={authRequirement}>{element}</PageGuard>;
};
