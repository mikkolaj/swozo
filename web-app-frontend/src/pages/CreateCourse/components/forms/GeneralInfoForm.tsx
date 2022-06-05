import { InputField } from 'common/Input/InputField';
import { Form, Formik, FormikProps } from 'formik';
import { FC, Ref } from 'react';

type CourseValues = {
    name: string;
    subject: string;
    description: string;
    numberOfActivities: number;
    numberOfStudents: number;
};

type Props = {
    formRef: Ref<FormikProps<CourseValues>>;
    initialValues: CourseValues;
    setCourseValues: (valuse: CourseValues) => void;
};

export const GeneralInfoForm: FC<Props> = ({ formRef, initialValues, setCourseValues }: Props) => {
    // const [numberOfStudents, setNumberOfStudents] = useState(0);

    return (
        <Formik
            innerRef={formRef}
            initialValues={initialValues}
            onSubmit={(values) => {
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
                    onChangeDecorator={() => {
                        // TODO
                        // if (!isNaN(+e.target.value)) setNumberOfStudents(+e.target.value);
                    }}
                />
            </Form>
        </Formik>
    );
};
