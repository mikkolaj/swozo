import { Button, Grid } from '@mui/material';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { FormikProps } from 'formik';
import _ from 'lodash';
import { Ref, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ActivitiesForm } from './components/forms/ActivitiesForm';
import { GeneralInfoForm } from './components/forms/GeneralInfoForm';
import { Summary } from './components/forms/Summary';

export type ActivityValues = {
    name: string;
    description: string;
    module: string;
    instructions: string;
};

export const CreateCourseView = () => {
    const { t } = useTranslation();
    const [currentSlide, setCurrentSlide] = useState(0);
    const [courseValues, setCourseValues] = useState({
        name: '',
        subject: '',
        description: '',
        numberOfActivities: 1,
        numberOfStudents: 0,
        students: ['', ''],
    });
    const [activitiesValues, setActivitiesValues] = useState<ActivityValues[]>([]);

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const formRef: Ref<FormikProps<any>> = useRef(null);

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
                                formRef.current?.handleSubmit();
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
                                    module: '',
                                    instructions: '',
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
                    initialValues={{ activities: activitiesValues }}
                    setValues={(values) => {
                        setActivitiesValues(values.activities);
                        setCurrentSlide(currentSlide + 1);
                    }}
                />
            )}
            {currentSlide === 2 && <Summary />}
        </SlideForm>
    );
};
