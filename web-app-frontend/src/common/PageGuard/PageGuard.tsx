import { PropsWithChildren, useEffect } from 'react';
import { Location, NavigateOptions, useLocation, useNavigate } from 'react-router-dom';
import { useAppSelector } from 'services/store';
import { AuthRequirement, hasRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';

const defaultRedirectRoute = (req: AuthRequirement): string => {
    return req.loggedIn ? PageRoutes.LOGIN : PageRoutes.HOME;
};

type Props = {
    authRequirement: AuthRequirement;
    redirectPath?: string;
    navigationOptionsProvider?: (guardedLocation: Location) => NavigateOptions;
};

export const PageGuard = ({
    authRequirement,
    redirectPath,
    navigationOptionsProvider,
    children,
}: PropsWithChildren<Props>) => {
    const navigate = useNavigate();
    const location = useLocation();
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
            navigate(
                redirectPath ?? defaultRedirectRoute(authRequirement),
                navigationOptionsProvider?.(location)
            );
        }
    }, [authState, navigate, authRequirement, redirectPath, navigationOptionsProvider, location]);

    return <>{children}</>;
};
