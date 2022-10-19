import { ErrorType } from 'api/errors';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { buildErrorHandler, ErrorHandler, HandlerConfig } from 'hooks/useApiErrorHandling';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { triggerError } from 'services/features/error/errorSlice';
import { useAppDispatch } from 'services/store';

export const buildErrorPageHandler = (navButtonMsg: string, navigateTo: string): ErrorHandler => {
    return buildErrorHandler(
        () => <PageContainerWithError navButtonMessage={navButtonMsg} navigateTo={navigateTo} />,
        true
    );
};

export const useFileErrorHandlers = () => {
    const dispatch = useAppDispatch();
    const { t } = useTranslation();
    const [commonHandlers] = useState<HandlerConfig>({
        ...Object.fromEntries(
            [ErrorType.THIRD_PARTY_ERROR, ErrorType.FILE_NOT_FOUND, ErrorType.DUPLICATE_FILE].map((err) => [
                err,
                buildErrorHandler(() => {
                    dispatch(triggerError({ message: t(`commonErrors.${err}`) }));
                }, false),
            ])
        ),
    });

    return commonHandlers;
};

export const useCommonErrorHandlers = () => {
    return useFileErrorHandlers();
};
