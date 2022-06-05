import { Box, Typography } from '@mui/material';
import { InputField } from 'common/Input/InputField';
import { Bar } from 'common/Styled/Bar';
import { FieldArray, Form, Formik, FormikProps } from 'formik';
import { ActivityValues } from 'pages/CreateCourse/CreateCourseView';
import { FC, Ref } from 'react';
import { useTranslation } from 'react-i18next';

type Values = {
    activities: ActivityValues[];
};

type Props = {
    formRef: Ref<FormikProps<Values>>;
    initialValues: ActivityValues[];
    setActivityValues: (values: ActivityValues[]) => void;
};

export const ActivitiesForm: FC<Props> = ({ formRef, initialValues, setActivityValues }: Props) => {
    const { t } = useTranslation();

    return (
        <Formik
            innerRef={formRef}
            initialValues={{ activities: initialValues }}
            validateOnChange={false}
            onSubmit={(values) => {
                setActivityValues(values.activities);
            }}
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
                                            <InputField
                                                wrapperSx={{ mb: 2 }}
                                                name={`activities.${idx}.name`}
                                                type="text"
                                                labelPath="createCourse.slides.1.form.name"
                                            />
                                            <InputField
                                                wrapperSx={{ mb: 2, width: '50%' }}
                                                name={`activities.${idx}.description`}
                                                type="text"
                                                textFieldProps={{ fullWidth: true, multiline: true }}
                                                labelPath="createCourse.slides.1.form.description"
                                            />
                                            <InputField
                                                wrapperSx={{ mb: 2 }}
                                                name={`activities.${idx}.module`}
                                                type="text"
                                                labelPath="createCourse.slides.1.form.modules"
                                            />
                                            <InputField
                                                wrapperSx={{ mb: 2, width: '50%' }}
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
