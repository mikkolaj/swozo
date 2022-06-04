import { InputField } from 'common/Input/InputField';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { Form, Formik, FormikProps } from 'formik';
import { Ref, useRef, useState } from 'react';

export const CreateCourseView = () => {
    const [currentSlide, setCurrentSlide] = useState(0);
    const [courseValues, setCourseValues] = useState({
        name: '',
        subject: '',
        description: '',
        numberOfActivities: 1,
        numberOfStudents: 0,
    });

    const formRef: Ref<FormikProps<typeof courseValues>> = useRef(null);

    return (
        <SlideForm
            titlePath="createCourse.title"
            slidesPath="createCourse.slides"
            slideCount={3}
            currentSlide={currentSlide}
            setSlide={(slideNum) => {
                formRef.current?.handleSubmit();
                setCurrentSlide(slideNum);
            }}
        >
            {currentSlide === 0 && (
                <Formik
                    innerRef={formRef}
                    initialValues={courseValues}
                    onSubmit={(values) => {
                        console.log(values);
                        setCourseValues(values);
                    }}
                >
                    <Form>
                        <InputField
                            wrapperSx={{ mb: 2 }}
                            name="name"
                            type="text"
                            labelPath="createCourse.slides.0.form.name"
                        />
                        <InputField
                            wrapperSx={{ mb: 2 }}
                            name="subject"
                            type="text"
                            labelPath="createCourse.slides.0.form.subject"
                        />
                        <InputField
                            wrapperSx={{ mb: 2, width: '50%' }}
                            name="description"
                            type="text"
                            textFieldProps={{ multiline: true, fullWidth: true }}
                            labelPath="createCourse.slides.0.form.description"
                        />
                        <InputField
                            wrapperSx={{ mb: 2 }}
                            name="numberOfActivities"
                            type="number"
                            labelPath="createCourse.slides.0.form.numberOfActivities"
                        />
                        <InputField
                            wrapperSx={{ mb: 2 }}
                            name="numberOfStudents"
                            type="number"
                            labelPath="createCourse.slides.0.form.numberOfStudents"
                        />
                    </Form>
                </Formik>
            )}
        </SlideForm>
    );
};
