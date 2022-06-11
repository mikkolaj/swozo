import { Grid, Typography } from '@mui/material';
import { InputField } from 'common/Input/InputField';
import { FieldArray, Form, Formik, FormikProps } from 'formik';
import { Ref } from 'react';
import * as Yup from 'yup';

type CourseValues = {
    name: string;
    subject: string;
    description: string;
    numberOfActivities: number;
    numberOfStudents: number;
    students: string[];
};

type Props = {
    formRef: Ref<FormikProps<CourseValues>>;
    initialValues: CourseValues;
    setCourseValues: (valuse: CourseValues) => void;
};

export const GeneralInfoForm = ({ formRef, initialValues, setCourseValues }: Props) => {
    // const [numberOfStudents, setNumberOfStudents] = useState(0);

    return (
        <Formik
            innerRef={formRef}
            initialValues={initialValues}
            validateOnChange={false}
            validationSchema={Yup.object({
                name: Yup.string().max(10, 'e1').required('e2'),
            })}
            onSubmit={(values) => {
                console.log(values);
                setCourseValues(values);
            }}
        >
            {({ values }) => (
                <Form>
                    <InputField
                        wrapperSx={{ mb: 2 }}
                        name="name"
                        type="text"
                        textFieldProps={{ required: true }}
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
                        textFieldProps={{ multiline: true, fullWidth: true, variant: 'outlined' }}
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
                    <Typography variant="subtitle1">Adresy Email uczestników</Typography>
                    <FieldArray
                        name="students"
                        render={(arrayHelpers) => (
                            <Grid container sx={{ mb: 2 }}>
                                {values.students.map((email, idx) => (
                                    <Grid item key={idx} xs={6}>
                                        <InputField
                                            name={`students.${idx}`}
                                            wrapperSx={{ mt: 2 }}
                                            type="text"
                                            labelPath=""
                                            textFieldProps={{
                                                onBlur: () => {
                                                    if (
                                                        values.students[idx] !== '' &&
                                                        idx === values.students.length - 2
                                                    ) {
                                                        arrayHelpers.push('');
                                                        arrayHelpers.push('');
                                                    }
                                                },
                                            }}
                                        />
                                    </Grid>
                                ))}
                            </Grid>
                        )}
                    />
                </Form>
            )}
        </Formik>
    );
};
