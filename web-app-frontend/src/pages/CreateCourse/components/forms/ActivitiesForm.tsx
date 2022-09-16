import { Box, Typography } from '@mui/material';
import { ServiceModuleDetailsDto } from 'api';
import { AutocompleteWithChips } from 'common/SlideForm/AutocompleteWithChips';
import { SlideProps } from 'common/SlideForm/SlideForm';
import { SlideFormDatePicker } from 'common/SlideForm/SlideFormDatePicker';
import { SlideFormInputField } from 'common/SlideForm/SlideFormInputField';
import { SlideFormTimePicker } from 'common/SlideForm/SlideFormTimePicker';
import { Bar } from 'common/Styled/Bar';
import { stylesRowWithSpaceBetweenItems } from 'common/styles';
import { FieldArray, Form, Formik } from 'formik';
import { ActivityValues } from 'pages/CreateCourse/util';
import { useTranslation } from 'react-i18next';
import * as Yup from 'yup';

type Values = {
    activities: ActivityValues[];
};

type Props = SlideProps<Values> & {
    availableLessonModules: ServiceModuleDetailsDto[];
    availableGeneralModules: ServiceModuleDetailsDto[];
};

export const ActivitiesForm = ({
    formRef,
    initialValues,
    setValues,
    availableLessonModules,
    availableGeneralModules,
}: Props) => {
    const { t } = useTranslation();

    return (
        <Formik
            innerRef={formRef}
            initialValues={initialValues}
            validationSchema={Yup.object().shape({
                activities: Yup.array().of(
                    Yup.object().shape({
                        // name: Yup.string().max(10, 'e1').required('e2'),
                    })
                ),
            })}
            validateOnChange={false}
            onSubmit={setValues}
        >
            {({ values, setFieldValue }) => (
                <Form>
                    <FieldArray
                        name="activities"
                        render={(_) => (
                            <Box>
                                {values.activities.map((value, idx) => (
                                    <Box key={idx} sx={{ mt: idx > 0 ? 8 : 4 }}>
                                        <Typography sx={{ ml: -4 }} variant="h5" component="div" gutterBottom>
                                            {t('createCourse.slides.1.form.activityHeader', {
                                                number: idx + 1,
                                            })}
                                        </Typography>

                                        <SlideFormInputField
                                            name={`activities.${idx}.name`}
                                            textFieldProps={{ fullWidth: true }}
                                            wrapperSx={{ width: '50%' }}
                                            type="text"
                                            i18nLabel="createCourse.slides.1.form.name"
                                        />

                                        <SlideFormInputField
                                            wrapperSx={{ width: '50%' }}
                                            name={`activities.${idx}.description`}
                                            type="text"
                                            textFieldProps={{ fullWidth: true, multiline: true }}
                                            i18nLabel="createCourse.slides.1.form.description"
                                        />

                                        <SlideFormDatePicker
                                            name={`activities.${idx}.startTime`}
                                            label={t('createCourse.slides.1.form.date')}
                                            value={value.date}
                                            setFieldValue={setFieldValue}
                                        />

                                        <Box sx={{ ...stylesRowWithSpaceBetweenItems, width: '50%' }}>
                                            <SlideFormTimePicker
                                                name={`activities.${idx}.startTime`}
                                                label={t('createCourse.slides.1.form.startTime')}
                                                value={value.startTime}
                                                setFieldValue={setFieldValue}
                                            />
                                            <SlideFormTimePicker
                                                name={`activities.${idx}.endTime`}
                                                label={t('createCourse.slides.1.form.endTime')}
                                                value={value.startTime}
                                                setFieldValue={setFieldValue}
                                            />
                                        </Box>

                                        <AutocompleteWithChips
                                            labelPath="createCourse.slides.1.form.lessonModules"
                                            name={`activities.${idx}.lessonModules`}
                                            chosenOptions={value.lessonModules}
                                            options={availableLessonModules}
                                            optionToString={({ name }) => name}
                                            setFieldValue={setFieldValue}
                                        />

                                        <AutocompleteWithChips
                                            labelPath="createCourse.slides.1.form.generalModules"
                                            name={`activities.${idx}.generalModules`}
                                            chosenOptions={value.generalModules}
                                            options={availableGeneralModules}
                                            optionToString={({ name }) => name}
                                            setFieldValue={setFieldValue}
                                        />

                                        <SlideFormInputField
                                            wrapperSx={{ width: '50%' }}
                                            name={`activities.${idx}.instructions`}
                                            type="text"
                                            textFieldProps={{ fullWidth: true, multiline: true }}
                                            i18nLabel="createCourse.slides.1.form.instructions"
                                        />
                                        {idx < values.activities.length - 1 && <Bar sx={{ mt: 4 }} />}
                                    </Box>
                                ))}
                            </Box>
                        )}
                    ></FieldArray>
                </Form>
            )}
        </Formik>
    );
};
