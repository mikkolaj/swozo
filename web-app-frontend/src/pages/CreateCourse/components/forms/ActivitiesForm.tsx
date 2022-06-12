import { Box, Typography } from '@mui/material';
import { SlideProps } from 'common/SlideForm/SlideForm';
import { SlideFormInputField } from 'common/SlideForm/SlideFormInputField';
import { Bar } from 'common/Styled/Bar';
import { FieldArray, Form, Formik } from 'formik';
import { ActivityValues } from 'pages/CreateCourse/CreateCourseView';
import { useTranslation } from 'react-i18next';
import * as Yup from 'yup';

type Values = {
    activities: ActivityValues[];
};

export const ActivitiesForm = ({ formRef, initialValues, setValues }: SlideProps<Values>) => {
    const { t } = useTranslation();

    return (
        <Formik
            innerRef={formRef}
            initialValues={initialValues}
            validationSchema={Yup.object().shape({
                activities: Yup.array().of(
                    Yup.object().shape({
                        name: Yup.string().max(10, 'e1').required('e2'),
                    })
                ),
            })}
            validateOnChange={false}
            onSubmit={setValues}
        >
            {({ values }) => (
                <Form>
                    <FieldArray
                        name="activities"
                        render={(_) => (
                            <Box>
                                {values.activities.length > 0 ? (
                                    values.activities.map((_, idx) => (
                                        <Box key={idx} sx={{ mt: idx > 0 ? 8 : 4 }}>
                                            <Typography
                                                sx={{ ml: -4 }}
                                                variant="h5"
                                                component="div"
                                                gutterBottom
                                            >
                                                {t('createCourse.slides.1.form.activityHeader', {
                                                    idx: idx + 1,
                                                })}
                                            </Typography>
                                            <SlideFormInputField
                                                name={`activities.${idx}.name`}
                                                textFieldProps={{ fullWidth: true }}
                                                wrapperSx={{ width: '50%' }}
                                                type="text"
                                                labelPath="createCourse.slides.1.form.name"
                                            />
                                            <SlideFormInputField
                                                wrapperSx={{ width: '50%' }}
                                                name={`activities.${idx}.description`}
                                                type="text"
                                                textFieldProps={{ fullWidth: true, multiline: true }}
                                                labelPath="createCourse.slides.1.form.description"
                                            />
                                            <SlideFormInputField
                                                name={`activities.${idx}.module`}
                                                type="text"
                                                labelPath="createCourse.slides.1.form.modules"
                                            />
                                            <SlideFormInputField
                                                wrapperSx={{ width: '50%' }}
                                                name={`activities.${idx}.instructions`}
                                                type="text"
                                                textFieldProps={{ fullWidth: true, multiline: true }}
                                                labelPath="createCourse.slides.1.form.instructions"
                                            />
                                            {idx < values.activities.length - 1 && (
                                                <Box mt={4}>
                                                    <Bar />
                                                </Box>
                                            )}
                                        </Box>
                                    ))
                                ) : (
                                    <div>empty</div>
                                )}
                            </Box>
                        )}
                    ></FieldArray>
                </Form>
            )}
        </Formik>
    );
};
