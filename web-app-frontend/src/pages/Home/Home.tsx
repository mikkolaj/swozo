import { AuthDetailsDtoRolesEnum } from 'api';
import { AdminPanel } from 'pages/Admin/AdminPanel';
import { useAppSelector } from 'services/store';
import { hasRole } from 'utils/roles';
import { UserHomePanel } from './UserHomePanel';

export const Home = () => {
    const auth = useAppSelector((state) => state.auth.authData);

    if (hasRole(auth, AuthDetailsDtoRolesEnum.Admin)) return <AdminPanel />;

    return <UserHomePanel />;
};
