import { ActivityDetailsDto } from 'api';
import { ApiError, ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { useEffect, useState } from 'react';
import { useErrorHandledQuery } from './query/useErrorHandledQuery';

export const useCourseWithActivity = (
    courseId: string,
    activityId: string,
    isApiErrorSet: (error: ApiError) => boolean,
    pushApiError: (error: ApiError) => void,
    removeApiError: (error: ApiError) => void
) => {
    const [activity, setActivity] = useState<ActivityDetailsDto>();
    const { data: course } = useErrorHandledQuery(
        ['courses', courseId],
        () => getApis().courseApi.getCourse({ id: +courseId }),
        pushApiError,
        removeApiError
    );

    useEffect(() => {
        const activity = course?.activities.find((activity) => activity.id === +activityId);
        const activityNotFoundError = { errorType: ErrorType.ACTIVITY_NOT_FOUND };

        if (activity) {
            setActivity(activity);
            if (isApiErrorSet(activityNotFoundError)) {
                removeApiError(activityNotFoundError);
            }
        } else if (course) {
            pushApiError(activityNotFoundError);
        }
    }, [course, activityId, pushApiError, removeApiError, isApiErrorSet]);

    return {
        course,
        activity,
    };
};
