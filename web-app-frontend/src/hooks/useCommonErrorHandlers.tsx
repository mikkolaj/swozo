import { ErrorType } from 'api/errors';
import { PageContainerWithError } from 'common/PageContainer/PageContainerWithError';
import { buildErrorHandler, ErrorHandler, HandlerConfig } from 'hooks/useApiErrorHandling';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { triggerError } from 'services/features/error/errorSlice';
import { useAppDispatch } from 'services/store';
import { PageRoutes } from 'utils/routes';

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

export const useNoCourseOrNoActivityErrorHandlers = (
    courseId: string,
    _activityId: string
): HandlerConfig => {
    const { t } = useTranslation();

    return {
        [ErrorType.COURSE_NOT_FOUND]: buildErrorPageHandler(
            t('activityInstructions.error.noCourse'),
            PageRoutes.MY_COURSES
        ),
        [ErrorType.ACTIVITY_NOT_FOUND]: buildErrorPageHandler(
            t('activityInstructions.error.noActivity'),
            PageRoutes.Course(courseId)
        ),
    };
};

export const useCommonErrorHandlers = () => {
    return useFileErrorHandlers();
};
