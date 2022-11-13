import { ActivityDetailsDto, ActivityModuleDetailsDto, CourseDetailsDto, EditCourseRequest } from 'api';
import { ApiError, ErrorType } from 'api/errors';
import { FormikProps } from 'formik';
import { t } from 'i18next';
import { argFormatter, CourseValues } from 'pages/CreateCourse/util';
import { QueryClient } from 'react-query';
import { prepareErrorForDisplay, prepareFormikValidationErrors } from 'utils/util';

function putIfDifferent<T>(oldVal: T, newVal: T): T | undefined {
    return oldVal === newVal ? undefined : newVal;
}

export const toEditCourseRequest = (
    oldValues: CourseDetailsDto,
    values: CourseValues
): EditCourseRequest => ({
    name: putIfDifferent(oldValues.name, values.name),
    description: putIfDifferent(oldValues.description, values.description),
    isPublic: putIfDifferent(oldValues.isPublic, values.isPublic),
    password: putIfDifferent(oldValues.coursePassword, values.password),
    subject: putIfDifferent(oldValues.subject, values.subject),
});

export const updateCourseCache = (queryClient: QueryClient, courseDetails: CourseDetailsDto) => {
    queryClient.setQueryData(['courses', `${courseDetails.id}`], courseDetails);
    queryClient.setQueryData(['courses'], (oldCourses: CourseDetailsDto[] = []) => {
        return [courseDetails, ...oldCourses.filter((course) => course.id !== courseDetails.id)];
    });
};

export function handleCourseUpdateError<T>(
    error: ApiError,
    pushApiError: (err: ApiError) => void,
    formik?: FormikProps<T> | null,
    keyMapper: (key: string) => string = (key) => key
) {
    if (error.errorType === ErrorType.VALIDATION_FAILED) {
        const errors = prepareFormikValidationErrors(error, keyMapper, (error) =>
            prepareErrorForDisplay(t, 'createCourse', error, argFormatter)
        );
        formik?.setErrors(errors);
    } else {
        pushApiError(error);
    }
}

export const setLinkDeliveryConfirmed = (
    course: CourseDetailsDto,
    activity: ActivityDetailsDto,
    activityModule: ActivityModuleDetailsDto
): CourseDetailsDto => ({
    ...course,
    activities: [
        ...(course?.activities.filter((ac) => ac !== activity) ?? []),
        {
            ...activity,
            activityModules: [
                ...activity.activityModules.filter((acm) => acm.id !== activityModule.id),
                {
                    ...activityModule,
                    linkConfirmed: true,
                },
            ],
        },
    ],
});
