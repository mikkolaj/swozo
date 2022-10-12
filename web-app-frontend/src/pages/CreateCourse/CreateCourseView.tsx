import { Grid } from '@mui/material';
import { CreateCourseRequest } from 'api';
import { ApiError, ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { NextSlideButton } from 'common/SlideForm/buttons/NextSlideButton';
import { PreviousSlideButton } from 'common/SlideForm/buttons/PreviousSlideButton';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { clearErrorsForSlide, getSortedSlidesWithErrors } from 'common/SlideForm/util';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikErrors, FormikProps } from 'formik';
import { useQueryWithDefaults } from 'hooks/query/useQueryWithDefaults';
import _ from 'lodash';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { mockGeneralModuleSummaryList } from 'utils/mocks';
import { PageRoutes } from 'utils/routes';
import * as Yup from 'yup';
import { ActivitiesForm, activityValidationSchema } from './components/forms/ActivitiesForm';
import { CourseInfoForm, courseValidationSchema } from './components/forms/CourseInfoForm';
import { Summary } from './components/forms/Summary';
import {
    ACTIVITIES_SLIDE,
    buildCreateCourseRequest,
    COURSE_SLIDE,
    formatErrors,
    FormValues,
    initialCourseValues,
    resizeActivityValuesList,
} from './util';

const initialValues: FormValues = {
    [COURSE_SLIDE]: initialCourseValues(),
    [ACTIVITIES_SLIDE]: {
        activities: [],
    },
};

export const CreateCourseView = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [currentSlide, setCurrentSlide] = useState(0);
    const [formattedApiErrors, setFormattedApiErrors] = useState<FormikErrors<FormValues>>();

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
            onError: (error: ApiError) => {
                if (error.errorType === ErrorType.VALIDATION_FAILED) {
                    const formattedErrors = formatErrors(t, error);
                    setFormattedApiErrors(formattedErrors);
                    setCurrentSlide(getSortedSlidesWithErrors(formattedErrors)[0]);
                } else {
                    // TODO
                }
            },
        }
    );

    useEffect(() => {
        if (formattedApiErrors) {
            formRef.current?.setErrors(formattedApiErrors);
        }
    }, [formattedApiErrors]);

    return (
        <SlideForm
            titleI18n="createCourse.title"
            slidesI18n="createCourse.slides"
            currentSlide={currentSlide}
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            innerRef={formRef as any}
            initialValues={initialValues}
            slidesWithErrors={getSortedSlidesWithErrors(formattedApiErrors ?? {})}
            validateOnChange={false}
            validateOnBlur={_.isEmpty(formattedApiErrors)}
            validationSchema={Yup.object({
                [COURSE_SLIDE]: Yup.object(courseValidationSchema),
                [ACTIVITIES_SLIDE]: Yup.object().shape({
                    activities: Yup.array().of(Yup.object(activityValidationSchema)),
                }),
            })}
            slideConstructors={[
                (slideProps) => <CourseInfoForm {...slideProps} />,
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
            onSubmit={(values) => {
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

                                if (formattedApiErrors) {
                                    setFormattedApiErrors(
                                        clearErrorsForSlide(formattedApiErrors, currentSlide)
                                    );
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
