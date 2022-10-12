import { FormInputField } from 'common/Input/FormInputField';
import { FormPasswordField } from 'common/Input/FormPasswordField';
import { SlideProps } from 'common/SlideForm/util';
import { CourseValues } from 'pages/CreateCourse/util';
import { ValidationSchema } from 'utils/types';
import * as Yup from 'yup';

export const courseValidationSchema: ValidationSchema<CourseValues> = {
    name: Yup.string().max(1000, 'e1').required('e2'),
};

export const CourseInfoForm = ({ nameBuilder }: SlideProps) => {
    return (
        <>
            <FormInputField
                wrapperSx={{ width: '50%' }}
                name={nameBuilder('name')}
                type="text"
                textFieldProps={{ fullWidth: true }}
                i18nLabel="createCourse.slides.0.form.name"
            />
            <FormInputField
                name={nameBuilder('subject')}
                type="text"
                i18nLabel="createCourse.slides.0.form.subject"
            />
            <FormInputField
                wrapperSx={{ width: '50%' }}
                name={nameBuilder('description')}
                type="text"
                textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                i18nLabel="createCourse.slides.0.form.description"
            />
            <FormInputField
                name={nameBuilder('numberOfActivities')}
                type="number"
                i18nLabel="createCourse.slides.0.form.numberOfActivities"
            />
            <FormInputField
                name={nameBuilder('expectedStudentCount')}
                type="number"
                i18nLabel="createCourse.slides.0.form.expectedStudentCount"
                onChangeDecorator={() => {
                    // TODO how this value affects array below
                    // if (!isNaN(+e.target.value)) setNumberOfStudents(+e.target.value);
                }}
            />

            <FormPasswordField
                name={nameBuilder('password')}
                i18nLabel="createCourse.slides.0.form.password"
            />

            {/* // TODO: this probably only complicates things, leaving it here just in case */}
            {/* <Divider sx={{ width: '75%', mt: 2, mb: 2 }} />
                    <Typography sx={{ mt: 0 }} variant="subtitle1">
                        Adresy Email uczestników
                    </Typography>
                    <FieldArray
                        name={nameBuilder("students")}
                        render={(arrayHelpers) => (
                            <Grid container sx={{ mb: 2 }}>
                                {values.students.map((email, idx) => (
                                    <Grid item key={idx} xs={6}>
                                        <InputField
                                            name={`students.${idx}`}
                                            wrapperSx={{ mt: 2 }}
                                            type="text"
                                            i18nLabel=""
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
                    /> */}
        </>
    );
};
