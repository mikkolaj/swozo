import { SlideForm } from 'common/SlideForm/SlideForm';
import { FormikProps } from 'formik';
import { Ref, useEffect, useRef, useState } from 'react';
import { range } from 'utils/utils';
import { ActivitiesForm } from './components/forms/ActivitiesForm';
import { GeneralInfoForm } from './components/forms/GeneralInfoForm';

export type ActivityValues = {
    name: string;
    description: string;
    module: string;
    instructions: string;
};

export const CreateCourseView = () => {
    const [currentSlide, setCurrentSlide] = useState(0);
    const [nextRequestedSlide, setNextRequestedSlide] = useState(0);
    const [courseValues, setCourseValues] = useState({
        name: '',
        subject: '',
        description: '',
        numberOfActivities: 1,
        numberOfStudents: 0,
    });
    const [activitiesValues, setActivitiesValues] = useState<ActivityValues[]>([]);

    useEffect(() => {
        if (currentSlide !== nextRequestedSlide) setCurrentSlide(nextRequestedSlide);
    }, [nextRequestedSlide, currentSlide]);

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const formRef: Ref<FormikProps<any>> = useRef(null);

    return (
        <SlideForm
            titlePath="createCourse.title"
            slidesPath="createCourse.slides"
            slideCount={3}
            currentSlide={currentSlide}
            setSlide={(_, next) => {
                formRef.current?.handleSubmit();
                setNextRequestedSlide(next);
            }}
        >
            {currentSlide === 0 && (
                <GeneralInfoForm
                    formRef={formRef}
                    initialValues={courseValues}
                    setCourseValues={(values) => {
                        setCourseValues(values);
                        if (values.numberOfActivities > activitiesValues.length) {
                            setActivitiesValues([
                                ...activitiesValues,
                                ...range(values.numberOfActivities - activitiesValues.length).map((_) => ({
                                    name: 'x',
                                    description: '',
                                    module: '',
                                    instructions: '',
                                })),
                            ]);
                        } else {
                            setActivitiesValues(activitiesValues.slice(0, values.numberOfActivities));
                        }
                    }}
                />
            )}
            {currentSlide === 1 && (
                <ActivitiesForm
                    formRef={formRef}
                    initialValues={activitiesValues}
                    setActivityValues={(values) => {
                        setActivitiesValues(values);
                    }}
                />
            )}
        </SlideForm>
    );
};
