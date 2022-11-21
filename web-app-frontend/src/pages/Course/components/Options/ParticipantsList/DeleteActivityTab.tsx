import { Box, Button, MenuItem } from '@mui/material';
import { ActivityDetailsDto, CourseDetailsDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { FormSelectField } from 'common/Input/FormSelectField';
import { stylesRowWithSpaceBetweenItems } from 'common/styles';
import dayjs from 'dayjs';
import { Form, Formik, FormikProps } from 'formik';
import { useCancelActivity } from 'hooks/query/useCancelActivity';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import _ from 'lodash';
import { updateCourseCache } from 'pages/Course/utils';
import { useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-toastify';
import { ConfirmationRequiredModal } from 'services/features/modal/modals/ConfirmationRequiredModal';

type Props = { course: CourseDetailsDto; onLastDeleted: () => void };

const selectedActivity = (course: CourseDetailsDto, activityId: number): ActivityDetailsDto =>
    course.activities.find((activity) => activity.id === activityId) ?? course.activities[0];

export const DeleteActivityTab = ({ course, onLastDeleted }: Props) => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();
    const { pushApiError } = useApiErrorHandling({});
    const [confirmationModalOpen, setConfirmationModalOpen] = useState(false);
    const [confirmationModalYesCallback, setConfirmationModalYesCallback] = useState<() => void>(
        () => undefined
    );
    const [justCancelMode, setJustCancelMode] = useState(true);
    const formRef = useRef<FormikProps<{ selectedActivityId: number }>>(null);
    const deletePermanentlyMutation = useMutation(
        (activityId: number) => getApis().courseApi.deleteActivity({ courseId: course.id, activityId }),
        {
            onSuccess: (courseDetails) => {
                updateCourseCache(queryClient, courseDetails);
                toast.success(t('toast.activityDeleted'));
                if (courseDetails.activities.length === 0) onLastDeleted();
                formRef.current?.setValues({ selectedActivityId: courseDetails.activities[0].id });
            },
            onError: pushApiError,
        }
    );
    const { cancelActivityMutation } = useCancelActivity(
        (activityDetails) =>
            updateCourseCache(queryClient, {
                ...course,
                activities: [
                    ...course.activities.filter((activity) => activity.id !== activityDetails.id),
                    activityDetails,
                ],
            }),
        pushApiError
    );

    return (
        <Box>
            <Formik
                initialValues={{ selectedActivityId: course.activities[0].id }}
                onSubmit={() => undefined}
                innerRef={formRef}
            >
                {({ values }) => (
                    <Box sx={{ width: '75%' }}>
                        <Form>
                            <FormSelectField
                                wrapperSx={{ width: '100%' }}
                                name={'selectedActivityId'}
                                i18nLabel="course.options.editor.tabs.deleteActivity.selectLabel"
                                textFieldProps={{ fullWidth: true }}
                            >
                                {_.sortBy(course.activities, (acitivity) => acitivity.startTime).map(
                                    ({ name, id }) => (
                                        <MenuItem key={id} value={id}>
                                            {name}
                                        </MenuItem>
                                    )
                                )}
                            </FormSelectField>
                            <Box sx={{ mt: 4, ...stylesRowWithSpaceBetweenItems }}>
                                <Button
                                    onClick={() => {
                                        setConfirmationModalYesCallback(
                                            () => () =>
                                                deletePermanentlyMutation.mutate(values.selectedActivityId)
                                        );
                                        setJustCancelMode(false);
                                        setConfirmationModalOpen(true);
                                    }}
                                    variant="contained"
                                    sx={{ p: 2 }}
                                >
                                    {t('course.options.editor.tabs.deleteActivity.deleteWithAllData')}
                                </Button>
                                {dayjs(selectedActivity(course, values.selectedActivityId).startTime).isAfter(
                                    dayjs()
                                ) &&
                                    !selectedActivity(course, values.selectedActivityId).cancelled && (
                                        <Button
                                            onClick={() => {
                                                setConfirmationModalYesCallback(
                                                    () => () =>
                                                        cancelActivityMutation.mutate(
                                                            values.selectedActivityId
                                                        )
                                                );
                                                setJustCancelMode(true);
                                                setConfirmationModalOpen(true);
                                            }}
                                            variant="contained"
                                            sx={{ p: 2 }}
                                        >
                                            {t('course.options.editor.tabs.deleteActivity.justCancel')}
                                        </Button>
                                    )}
                            </Box>
                        </Form>
                    </Box>
                )}
            </Formik>
            <ConfirmationRequiredModal
                open={confirmationModalOpen}
                noText={t('course.options.editor.tabs.deleteActivity.no')}
                yesText={t('course.options.editor.tabs.deleteActivity.yes')}
                onClose={() => setConfirmationModalOpen(false)}
                onYes={() => {
                    setConfirmationModalOpen(false);
                    confirmationModalYesCallback();
                }}
                textLines={[
                    t(
                        `course.options.editor.tabs.deleteActivity.${
                            justCancelMode ? 'confirmCancel' : 'confirmDeletion'
                        }`
                    ),
                ]}
                yesPreffered={false}
            />
        </Box>
    );
};
