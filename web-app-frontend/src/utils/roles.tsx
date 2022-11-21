import { AuthDetailsDto, AuthDetailsDtoRolesEnum, UserDetailsDto } from 'api';
import { PageGuard } from 'common/PageGuard/PageGuard';
import { useAppSelector } from 'services/store';

export const STUDENT = AuthDetailsDtoRolesEnum.Student;
export const TEACHER = AuthDetailsDtoRolesEnum.Teacher;
export const TECHNICAL_TEACHER = AuthDetailsDtoRolesEnum.TechnicalTeacher;
export const ADMIN = AuthDetailsDtoRolesEnum.Admin;
export const ANY_EXCEPT_ADMIN = [STUDENT, TEACHER, TECHNICAL_TEACHER];

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
    roles: Object.values(AuthDetailsDtoRolesEnum),
};

export const withRole = (...roles: AuthDetailsDtoRolesEnum[]): AuthRequirement => {
    return {
        loggedIn: true,
        roles,
    };
};

export const hasRole = (authData?: AuthDetailsDto, ...roles: AuthDetailsDtoRolesEnum[]): boolean => {
    if (!authData) return false;
    if (authData.roles.includes(AuthDetailsDtoRolesEnum.Admin)) return true;
    return roles.find((role) => authData.roles.includes(role)) !== undefined;
};

export const WithRole = ({
    children,
    roles,
}: React.PropsWithChildren<{ roles: AuthDetailsDtoRolesEnum[] }>) => {
    const auth = useAppSelector((state) => state.auth.authData);

    return roles.length > 0 ? <>{hasRole(auth, ...roles) && children}</> : <>{children}</>;
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

export const useRoles = () => {
    const auth = useAppSelector((state) => state.auth);
    return auth.isLoggedIn && auth.authData ? auth.authData.roles : [];
};

export const isSame = (user1?: UserDetailsDto, user2?: UserDetailsDto) => {
    if (!user1 || !user2) return false;
    return user1.email === user2.email && user1.name === user2.name && user1.surname === user2.surname;
};
