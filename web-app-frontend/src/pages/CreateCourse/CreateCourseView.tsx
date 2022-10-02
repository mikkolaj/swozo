import { Grid } from '@mui/material';
import { CreateCourseRequest } from 'api';
import { getApis } from 'api/initialize-apis';
import { NextSlideButton } from 'common/SlideForm/NextSlideButton';
import { PreviousSlideButton } from 'common/SlideForm/PreviousSlideButton';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikProps } from 'formik';
import { useQueryWithDefaults } from 'hooks/query/useQueryWithDefaults';
import { useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { mockGeneralModuleSummaryList } from 'utils/mocks';
import { PageRoutes } from 'utils/routes';
import * as Yup from 'yup';
import { ActivitiesForm, activityValidationSchema } from './components/forms/ActivitiesForm';
import { courseValidationSchema, GeneralInfoForm } from './components/forms/GeneralInfoForm';
import { Summary } from './components/forms/Summary';
import {
    ACTIVITIES_SLIDE,
    buildCreateCourseRequest,
    COURSE_SLIDE,
    FormValues,
    initialCourseValues,
    resizeActivityValuesList,
} from './util';

export const CreateCourseView = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [currentSlide, setCurrentSlide] = useState(0);
    const initialValues: FormValues = {
        [COURSE_SLIDE]: initialCourseValues(),
        [ACTIVITIES_SLIDE]: {
            activities: [],
        },
    };

    const formRef = useRef<FormikProps<FormValues>>(null);
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

    return (
        <SlideForm
            titleI18n="createCourse.title"
            slidesI18n="createCourse.slides"
            currentSlide={currentSlide}
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            innerRef={formRef as any}
            initialValues={initialValues}
            validateOnChange={false}
            validationSchema={Yup.object({
                [COURSE_SLIDE]: Yup.object(courseValidationSchema),
                [ACTIVITIES_SLIDE]: Yup.object().shape({
                    activities: Yup.array().of(Yup.object(activityValidationSchema)),
                }),
            })}
            slideConstructors={[
                (slideProps) => <GeneralInfoForm {...slideProps} />,
                (slideProps, { values, setFieldValue }) => (
                    <ActivitiesForm
                        {...slideProps}
                        values={values[ACTIVITIES_SLIDE]}
                        setFieldValue={setFieldValue}
                        availableLessonModules={availableLessonModules}
                        availableGeneralModules={availableGeneralModules}
                    />
                ),
                (_, { values }) => (
                    <Summary course={values[COURSE_SLIDE]} activities={values[ACTIVITIES_SLIDE].activities} />
                ),
            ]}
            onSubmit={() => {
                console.log('submit');
                const values = formRef.current?.values;
                if (!values) return;

                createCourseMutation.mutate(
                    buildCreateCourseRequest(values[COURSE_SLIDE], values[ACTIVITIES_SLIDE].activities)
                );
            }}
            buttons={
                <Grid container>
                    <Grid item xs={6}>
                        <PreviousSlideButton
                            currentSlide={currentSlide}
                            label={t('createCourse.buttons.back')}
                            onBack={setCurrentSlide}
                        />
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <NextSlideButton
                            currentSlide={currentSlide}
                            slideCount={3}
                            label={t('createCourse.buttons.next')}
                            lastSlideLabel={t('createCourse.finish')}
                            slideValidator={formRef.current ?? undefined}
                            onNext={(toSlide) => {
                                const values = formRef.current?.values;
                                if (values && toSlide === 1) {
                                    formRef.current?.setValues({
                                        ...values,
                                        [ACTIVITIES_SLIDE]: {
                                            activities: resizeActivityValuesList(
                                                values[ACTIVITIES_SLIDE].activities,
                                                values[COURSE_SLIDE].numberOfActivities
                                            ),
                                        },
                                    });
                                }

                                setCurrentSlide(toSlide);
                            }}
                            onFinish={() => {
                                formRef.current?.handleSubmit();
                            }}
                        />
                    </Grid>
                </Grid>
            }
        />
    );
};
