import { Box, Button, MenuItem } from '@mui/material';
import { CourseDetailsDto } from 'api';
import { FormSelectField } from 'common/Input/FormSelectField';
import { stylesRowWithSpaceBetweenItems } from 'common/styles';
import dayjs from 'dayjs';
import { Form, Formik } from 'formik';
import _ from 'lodash';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ConfirmationRequiredModal } from 'services/features/modal/modals/ConfirmationRequiredModal';

type Props = { course: CourseDetailsDto };

export const DeleteActivityTab = ({ course }: Props) => {
    const { t } = useTranslation();
    // const { pushApiError, removeApiError, isApiErrorSet } = useApiErrorHandling({});
    const [confirmationModalOpen, setConfirmationModalOpen] = useState(false);
    const [confirmationModalYesCallback, setConfirmationModalYesCallback] = useState<() => void>(
        () => undefined
    );
    const [justCancelMode, setJustCancelMode] = useState(true);
    const deletePermanentlyMutation = (x: number) => console.log('delete ', x);
    const cancelMutation = (x: number) => console.log('cancel ', x);
    return (
        <Box>
            <Formik
                initialValues={{ selectedActivityId: course.activities[0].id }}
                onSubmit={() => undefined}
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
                                            () => () => deletePermanentlyMutation(values.selectedActivityId)
                                        );
                                        setJustCancelMode(false);
                                        setConfirmationModalOpen(true);
                                    }}
                                    variant="contained"
                                    sx={{ p: 2 }}
                                >
                                    {t('course.options.editor.tabs.deleteActivity.deleteWithAllData')}
                                </Button>
                                {dayjs(
                                    (
                                        course.activities.find(
                                            (activity) => activity.id === values.selectedActivityId
                                        ) ?? course.activities[0]
                                    ).startTime
                                ).isBefore(dayjs()) && (
                                    <Button
                                        onClick={() => {
                                            setConfirmationModalYesCallback(
                                                () => () => cancelMutation(values.selectedActivityId)
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
