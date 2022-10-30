import { CourseDetailsDto, CreateActivityRequest } from 'api';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { FormikProps } from 'formik';
import { handleCourseUpdateError, updateCourseCache } from 'pages/Course/utils';
import { ActivityValues, FIELD_SEPARATOR } from 'pages/CreateCourse/util';
import { MutableRefObject } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-toastify';

export const useAddSingleActivity = (
    course: CourseDetailsDto,
    formRef: MutableRefObject<FormikProps<{ activities: ActivityValues[] }> | null>,
    pushApiError: (error: ApiError) => void
) => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();

    const addActivityMutation = useMutation(
        (createActivityRequest: CreateActivityRequest) =>
            getApis().courseApi.addSingleActivity({ id: course.id, createActivityRequest }),
        {
            onSuccess: (courseDetails) => {
                toast.success(t('toast.activityAdded'));
                updateCourseCache(queryClient, courseDetails);
            },
            onError: (error: ApiError) => {
                handleCourseUpdateError(
                    error,
                    pushApiError,
                    formRef.current,
                    (key) => `activities${FIELD_SEPARATOR}0${FIELD_SEPARATOR}${key}`
                );
            },
        }
    );

    return { addActivityMutation };
};
