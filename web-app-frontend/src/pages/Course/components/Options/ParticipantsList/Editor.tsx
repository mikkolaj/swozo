import { Box, Button, Tab, Tabs } from '@mui/material';
import { CourseDetailsDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { stylesColumnCenteredVertical, stylesRow } from 'common/styles';
import { Form, Formik, FormikProps } from 'formik';
import { useAddSingleActivity } from 'hooks/query/useAddSingleActivity';
import { useEditCourse } from 'hooks/query/useEditCourse';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { toEditCourseRequest } from 'pages/Course/utils';
import { ActivitiesForm } from 'pages/CreateCourse/components/forms/ActivitiesForm';
import { CourseInfoForm } from 'pages/CreateCourse/components/forms/CourseInfoForm';
import {
    ActivityValues,
    buildCreateActivityRequest,
    CourseValues,
    initialActivityValues,
    toCourseValues,
} from 'pages/CreateCourse/util';
import { useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';

type Props = {
    course: CourseDetailsDto;
};

type Options = 'course' | 'addActivity';

export const Editor = ({ course }: Props) => {
    const { t } = useTranslation();
    const { pushApiError, removeApiError } = useApiErrorHandling({});
    const [option, setOption] = useState<Options>('course');
    const courseFormRef = useRef<FormikProps<CourseValues>>(null);
    const activityFormRef = useRef<FormikProps<{ activities: ActivityValues[] }>>(null);
    const { data: availableLessonModules } = useErrorHandledQuery(
        ['modules', 'summary', 'public'],
        () => getApis().serviceModuleApi.getAllPublicServiceModules(),
        pushApiError,
        removeApiError
    );

    const { editCourseMutation } = useEditCourse(course, courseFormRef, pushApiError);
    const { addActivityMutation } = useAddSingleActivity(course, activityFormRef, pushApiError);

    return (
        <Box sx={{ ...stylesRow, width: '100%' }}>
            <Tabs
                sx={{
                    ml: -2,
                    width: '20%',
                    textAlign: 'left',
                    '.MuiTabs-indicator': {
                        left: 0,
                    },
                }}
                value={option}
                onChange={(_, tab) => setOption(tab)}
                orientation="vertical"
            >
                <Tab
                    sx={{ textAlign: 'left' }}
                    value={'course'}
                    label={t('course.options.editor.tabs.course.label')}
                />
                <Tab value="addActivity" label={t('course.options.editor.tabs.addActivity.label')} />
            </Tabs>

            <Box sx={{ width: '100%', ml: 6 }}>
                <Box sx={{ width: '100%' }}>
                    {option === 'course' && (
                        <Formik
                            innerRef={courseFormRef}
                            initialValues={toCourseValues(course)}
                            onSubmit={(values) =>
                                editCourseMutation.mutate(toEditCourseRequest(course, values))
                            }
                        >
                            {({ handleChange, values }) => (
                                <Form>
                                    <Box sx={{ ...stylesColumnCenteredVertical }}>
                                        <CourseInfoForm
                                            createMode={false}
                                            nameBuilder={(name) => name}
                                            values={values}
                                            handleChange={handleChange}
                                        />
                                        <Box sx={{ margin: 'auto', mt: 5 }}>
                                            <Button
                                                sx={{ p: 2, minWidth: '200px' }}
                                                type="submit"
                                                disabled={editCourseMutation.isLoading}
                                                variant="contained"
                                            >
                                                {t('course.options.editor.tabs.course.button')}
                                            </Button>
                                        </Box>
                                    </Box>
                                </Form>
                            )}
                        </Formik>
                    )}
                    {option === 'addActivity' && (
                        <Formik
                            initialValues={{ activities: [initialActivityValues()] }}
                            onSubmit={(values) =>
                                addActivityMutation.mutate(buildCreateActivityRequest(values.activities[0]))
                            }
                            innerRef={activityFormRef}
                        >
                            {({ setFieldValue, setValues, values }) => (
                                <Form>
                                    <Box sx={{ ...stylesColumnCenteredVertical }}>
                                        <ActivitiesForm
                                            nameBuilder={(name) => name}
                                            values={values}
                                            setFieldValue={setFieldValue}
                                            availableLessonModules={availableLessonModules ?? []}
                                            availableGeneralModules={[]}
                                            createMode={false}
                                        />
                                        <Box sx={{ mt: 5, ...stylesRow }}>
                                            <Button
                                                sx={{ p: 2, minWidth: '200px', mr: 5 }}
                                                onClick={() =>
                                                    setValues({ activities: [initialActivityValues()] })
                                                }
                                                disabled={addActivityMutation.isLoading}
                                                variant="outlined"
                                            >
                                                {t('course.options.editor.tabs.addActivity.secondaryButton')}
                                            </Button>

                                            <Button
                                                sx={{ p: 2, minWidth: '200px' }}
                                                type="submit"
                                                disabled={addActivityMutation.isLoading}
                                                variant="contained"
                                            >
                                                {t('course.options.editor.tabs.addActivity.button')}
                                            </Button>
                                        </Box>
                                    </Box>
                                </Form>
                            )}
                        </Formik>
                    )}
                </Box>
            </Box>
        </Box>
    );
};
