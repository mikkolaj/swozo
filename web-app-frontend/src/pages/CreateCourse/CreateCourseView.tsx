import { Button, Grid } from '@mui/material';
import { CreateCourseRequest } from 'api';
import { getApis } from 'api/initialize-apis';
import { SlideForm } from 'common/SlideForm/SlideForm';
import dayjs from 'dayjs';
import { FormikProps } from 'formik';
import _ from 'lodash';
import { Ref, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQuery, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { mockGeneralModuleSummaryList } from 'utils/mocks';
import { PageRoutes } from 'utils/routes';
import { ActivitiesForm } from './components/forms/ActivitiesForm';
import { GeneralInfoForm } from './components/forms/GeneralInfoForm';
import { Summary } from './components/forms/Summary';
import { ActivityValues, buildCreateCourseRequest } from './util';

export const CreateCourseView = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [currentSlide, setCurrentSlide] = useState(0);
    const [courseValues, setCourseValues] = useState({
        name: 'Wprowadzenie do Pythona',
        subject: 'Informatyka',
        description: '',
        numberOfActivities: 1,
        numberOfStudents: 2,
        students: ['student1', ''],
    });
    const [activitiesValues, setActivitiesValues] = useState<ActivityValues[]>([]);

    const { data: availableLessonModules } = useQuery('modules', () =>
        getApis().serviceModuleApi.getModuleList()
    );
    const [availableGeneralModules] = useState(mockGeneralModuleSummaryList);

    const queryClient = useQueryClient();

    const createCourseMutation = useMutation(
        (createCourseRequest: CreateCourseRequest) => getApis().courseApi.addCourse({ createCourseRequest }),
        {
            onSuccess: (courseDetailsResp) => {
                // TODO update instead of invalidation
                queryClient.invalidateQueries('courses');
                toast(t('toast.courseCreated'));
                navigate(PageRoutes.Course(courseDetailsResp.id));
            },
        }
    );

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const formRef: Ref<FormikProps<any>> = useRef(null);
    //TODO
    if (availableLessonModules === undefined) {
        return <>Loading</>;
    }

    return (
        <SlideForm
            titlePath="createCourse.title"
            slidesPath="createCourse.slides"
            slideCount={3}
            currentSlide={currentSlide}
            buttons={
                <Grid container>
                    <Grid item xs={6}>
                        {currentSlide > 0 && (
                            <Button
                                onClick={() => {
                                    if (currentSlide === 1) {
                                        setActivitiesValues(formRef.current?.values.activities);
                                    }
                                    setCurrentSlide(currentSlide - 1);
                                }}
                            >
                                {t('createCourse.buttons.back')}
                            </Button>
                        )}
                    </Grid>
                    <Grid
                        item
                        xs={6}
                        sx={{
                            display: 'flex',
                            flexDirection: 'row',
                            justifyContent: 'flex-end',
                        }}
                    >
                        <Button
                            sx={{ alignSelf: 'flex-end' }}
                            onClick={() => {
                                // TODO refactor this
                                if (currentSlide === 2) {
                                    // TODO assert valid
                                    createCourseMutation.mutate(
                                        buildCreateCourseRequest(courseValues, activitiesValues)
                                    );
                                } else {
                                    formRef.current?.handleSubmit();
                                }
                            }}
                        >
                            {t(currentSlide === 2 ? 'createCourse.finish' : 'createCourse.buttons.next')}
                        </Button>
                    </Grid>
                </Grid>
            }
        >
            {currentSlide === 0 && (
                <GeneralInfoForm
                    formRef={formRef}
                    initialValues={courseValues}
                    setValues={(values) => {
                        setCourseValues(values);
                        if (values.numberOfActivities > activitiesValues.length) {
                            setActivitiesValues([
                                ...activitiesValues,
                                ..._.range(values.numberOfActivities - activitiesValues.length).map((_) => ({
                                    name: '',
                                    description: '',
                                    lessonModules: [],
                                    generalModules: [],
                                    instructions: '',
                                    startTime: dayjs(),
                                    endTime: dayjs().add(90, 'minutes'), // TODO assert same day
                                })),
                            ]);
                        } else {
                            setActivitiesValues(activitiesValues.slice(0, values.numberOfActivities));
                        }

                        setCurrentSlide(currentSlide + 1);
                    }}
                />
            )}
            {currentSlide === 1 && (
                <ActivitiesForm
                    formRef={formRef}
                    availableLessonModules={availableLessonModules}
                    availableGeneralModules={availableGeneralModules}
                    initialValues={{ activities: activitiesValues }}
                    setValues={(values) => {
                        setActivitiesValues(values.activities);
                        setCurrentSlide(currentSlide + 1);
                    }}
                />
            )}
            {currentSlide === 2 && <Summary course={courseValues} activities={activitiesValues} />}
        </SlideForm>
    );
};
