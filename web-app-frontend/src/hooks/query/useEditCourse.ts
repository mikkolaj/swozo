import { CourseDetailsDto, EditCourseRequest } from 'api';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { FormikProps } from 'formik';
import { handleCourseUpdateError, updateCourseCache } from 'pages/Course/utils';
import { CourseValues } from 'pages/CreateCourse/util';
import { MutableRefObject } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-toastify';

export const useEditCourse = (
    course: CourseDetailsDto,
    formRef: MutableRefObject<FormikProps<CourseValues> | null>,
    pushApiError: (error: ApiError) => void
) => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();

    const editCourseMutation = useMutation(
        (editCourseRequest: EditCourseRequest) =>
            getApis().courseApi.editCourse({ id: course.id, editCourseRequest }),
        {
            onSuccess: (courseDetails) => {
                toast.success(t('toast.courseUpdated'));
                updateCourseCache(queryClient, courseDetails);
            },
            onError: (error: ApiError) => {
                handleCourseUpdateError(error, pushApiError, formRef.current);
            },
        }
    );

    return { editCourseMutation };
};
