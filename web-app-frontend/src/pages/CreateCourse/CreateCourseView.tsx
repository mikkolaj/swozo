import { Grid } from '@mui/material';
import { CreateCourseRequest } from 'api';
import { getApis } from 'api/initialize-apis';
import { NextSlideButton } from 'common/SlideForm/NextSlideButton';
import { PreviousSlideButton } from 'common/SlideForm/PreviousSlideButton';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikProps } from 'formik';
import { useQueryWithDefaults } from 'hooks/useQueryWithDefaults';
import { useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { mockGeneralModuleSummaryList } from 'utils/mocks';
import { PageRoutes } from 'utils/routes';
import { ActivitiesForm } from './components/forms/ActivitiesForm';
import { GeneralInfoForm } from './components/forms/GeneralInfoForm';
import { Summary } from './components/forms/Summary';
import {
    ActivityValues,
    buildCreateCourseRequest,
    initialCourseValues,
    resizeActivityValuesList,
} from './util';

const SLIDE_COUNT = 3;

export const CreateCourseView = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [currentSlide, setCurrentSlide] = useState(0);
    const [courseValues, setCourseValues] = useState(initialCourseValues());
    const [activitiesValues, setActivitiesValues] = useState<ActivityValues[]>([]);

    const { data: availableLessonModules } = useQueryWithDefaults(
        'modules',
        () => getApis().serviceModuleApi.getModuleList(),
        []
    );

    // TODO: call api
    const [availableGeneralModules] = useState(mockGeneralModuleSummaryList);

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
    const formRef = useRef<FormikProps<any>>(null);

    return (
        <SlideForm
            titleI18n="createCourse.title"
            slidesI18n="createCourse.slides"
            slideCount={SLIDE_COUNT}
            currentSlide={currentSlide}
            buttons={
                <Grid container>
                    <Grid item xs={6}>
                        <PreviousSlideButton
                            currentSlide={currentSlide}
                            label={t('createCourse.buttons.back')}
                            goBack={(toSlide) => {
                                if (toSlide === 0) {
                                    setActivitiesValues(formRef.current?.values.activities);
                                }

                                setCurrentSlide(toSlide);
                            }}
                        />
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <NextSlideButton
                            currentSlide={currentSlide}
                            slideCount={SLIDE_COUNT}
                            label={t('createCourse.buttons.next')}
                            lastSlideLabel={t('createCourse.finish')}
                            goNext={() => formRef.current?.handleSubmit()}
                            finish={() => {
                                // TODO assert valid
                                createCourseMutation.mutate(
                                    buildCreateCourseRequest(courseValues, activitiesValues)
                                );
                            }}
                        />
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
                        setActivitiesValues(
                            resizeActivityValuesList(activitiesValues, values.numberOfActivities)
                        );
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
