import { Divider, Grid, Typography } from '@mui/material';
import { InputField } from 'common/Input/InputField';
import { SlideProps } from 'common/SlideForm/SlideForm';
import { SlideFormInputField } from 'common/SlideForm/SlideFormInputField';
import { FieldArray, Form, Formik } from 'formik';
import * as Yup from 'yup';

type CourseValues = {
    name: string;
    subject: string;
    description: string;
    numberOfActivities: number;
    numberOfStudents: number;
    students: string[];
};

export const GeneralInfoForm = ({ formRef, initialValues, setValues }: SlideProps<CourseValues>) => {
    // const [numberOfStudents, setNumberOfStudents] = useState(0);

    return (
        <Formik
            innerRef={formRef}
            initialValues={initialValues}
            validateOnChange={false}
            validationSchema={Yup.object({
                // name: Yup.string().max(10, 'e1').required('e2'),
            })}
            onSubmit={setValues}
        >
            {({ values }) => (
                <Form>
                    <SlideFormInputField
                        wrapperSx={{ width: '50%' }}
                        name="name"
                        type="text"
                        textFieldProps={{ fullWidth: true }}
                        labelPath="createCourse.slides.0.form.name"
                    />
                    <SlideFormInputField
                        name="subject"
                        type="text"
                        labelPath="createCourse.slides.0.form.subject"
                    />
                    <SlideFormInputField
                        wrapperSx={{ width: '50%' }}
                        name="description"
                        type="text"
                        textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                        labelPath="createCourse.slides.0.form.description"
                    />
                    <SlideFormInputField
                        name="numberOfActivities"
                        type="number"
                        labelPath="createCourse.slides.0.form.numberOfActivities"
                    />
                    <SlideFormInputField
                        name="numberOfStudents"
                        type="number"
                        labelPath="createCourse.slides.0.form.numberOfStudents"
                        onChangeDecorator={() => {
                            // TODO how this value affects array below
                            // if (!isNaN(+e.target.value)) setNumberOfStudents(+e.target.value);
                        }}
                    />
                    {/* // TODO how to style this */}
                    <Divider sx={{ width: '75%', mt: 2 }} />
                    <Typography sx={{ mt: 0 }} variant="subtitle1">
                        Adresy Email uczestników
                    </Typography>
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
