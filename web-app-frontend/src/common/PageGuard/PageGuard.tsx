import { PropsWithChildren, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppSelector } from 'services/store';
import { AuthRequirement, hasRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';

const getDefaultRedirectRoute = (req: AuthRequirement): string => {
    return req.loggedIn ? PageRoutes.LOGIN : PageRoutes.HOME;
};

type Props = {
    authRequirement: AuthRequirement;
    redirectPath?: string;
};

export const PageGuard = ({ authRequirement, redirectPath, children }: PropsWithChildren<Props>) => {
    const navigate = useNavigate();
    const authState = useAppSelector((state) => state.auth);

    useEffect(() => {
        let shouldNavigate = false;

        if (!authRequirement.loggedIn) {
            shouldNavigate = authState.isLoggedIn;
        } else {
            if (authState.isLoggedIn && authState.authData) {
                shouldNavigate = !hasRole(authState.authData, ...authRequirement.roles);
            } else {
                shouldNavigate = true;
            }
        }

        if (shouldNavigate) {
            navigate(redirectPath ?? getDefaultRedirectRoute(authRequirement));
        }
    }, [authState, navigate, authRequirement, redirectPath]);

    return <>{children}</>;
};
