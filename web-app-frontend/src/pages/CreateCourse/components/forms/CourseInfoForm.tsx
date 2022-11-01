import { Checkbox, FormControlLabel } from '@mui/material';
import { FormInputField } from 'common/Input/FormInputField';
import { FormPasswordField } from 'common/Input/FormPasswordField';
import { SlideProps } from 'common/SlideForm/util';
import { CourseValues } from 'pages/CreateCourse/util';
import { ChangeEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { ValidationSchema } from 'utils/types';
import * as Yup from 'yup';

export const courseValidationSchema: ValidationSchema<CourseValues> = {
    name: Yup.string().max(1000, 'e1').required('e2'),
};

type Props = SlideProps & {
    values: CourseValues;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    handleChange: (e: ChangeEvent<any>) => void;
    createMode?: boolean;
};

export const CourseInfoForm = ({ nameBuilder, handleChange, values, createMode = true }: Props) => {
    const { t } = useTranslation();
    const width = createMode ? '50%' : '55%';

    return (
        <>
            <FormInputField
                wrapperSx={{ width }}
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
                wrapperSx={{ width }}
                name={nameBuilder('description')}
                type="text"
                textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                i18nLabel="createCourse.slides.0.form.description"
            />
            {createMode && (
                <FormInputField
                    name={nameBuilder('numberOfActivities')}
                    type="number"
                    i18nLabel="createCourse.slides.0.form.numberOfActivities"
                />
            )}
            {createMode && (
                <FormInputField
                    name={nameBuilder('expectedStudentCount')}
                    type="number"
                    i18nLabel="createCourse.slides.0.form.expectedStudentCount"
                    onChangeDecorator={() => {
                        // TODO how this value affects array below
                        // if (!isNaN(+e.target.value)) setNumberOfStudents(+e.target.value);
                    }}
                />
            )}
            <FormPasswordField
                name={nameBuilder('password')}
                i18nLabel="createCourse.slides.0.form.password"
            />
            <FormControlLabel
                sx={{ mt: 2 }}
                control={<Checkbox checked={values.isPublic} value={values.isPublic} />}
                label={t('createCourse.slides.0.form.public')}
                name={nameBuilder('isPublic')}
                onChange={handleChange}
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
