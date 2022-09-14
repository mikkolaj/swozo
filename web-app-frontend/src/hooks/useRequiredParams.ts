import { useNavigate, useParams } from 'react-router-dom';

export const useRequiredParams = (paramNames: string[], navigateTo: string = '/'): string[] => {
    const navigate = useNavigate();
    const params = useParams();
    const res: string[] = [];

    for (const name of paramNames) {
        const param = params[name];
        if (param !== undefined) {
            res.push(param);
        } else {
            console.error(`Required route param: ${name} not found`);
            navigate(navigateTo, { replace: true });
            return [];
        }
    }

    return res;
};
