import { Box, Typography } from '@mui/material';
import { DesktopDatePicker } from '@mui/x-date-pickers/DesktopDatePicker';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import { ServiceModuleDetailsResp } from 'api';
import { AutocompleteWithChips } from 'common/SlideForm/AutocompleteWithChips';
import { SlideProps } from 'common/SlideForm/SlideForm';
import { SlideFormInputField } from 'common/SlideForm/SlideFormInputField';
import { Bar } from 'common/Styled/Bar';
import { FieldArray, Form, Formik } from 'formik';
import { ActivityValues } from 'pages/CreateCourse/util';
import { useTranslation } from 'react-i18next';
import * as Yup from 'yup';

type Values = {
    activities: ActivityValues[];
};

type Props = SlideProps<Values> & {
    availableLessonModules: ServiceModuleDetailsResp[];
    availableGeneralModules: ServiceModuleDetailsResp[];
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
                                        <DesktopDatePicker
                                            label={t('createCourse.slides.1.form.date')}
                                            inputFormat="DD/MM/YYYY"
                                            value={value.startTime}
                                            onChange={(v) => {
                                                setFieldValue(`activities.${idx}.startTime`, v);
                                                setFieldValue(`activities.${idx}.endTime`, v);
                                            }}
                                            renderInput={({ name: _name, ...params }) => (
                                                <SlideFormInputField
                                                    name={`activities.${idx}.startTime`}
                                                    textFieldProps={{
                                                        sx: { width: '230px' },
                                                        ...params,
                                                    }}
                                                />
                                            )}
                                        />
                                        <Box
                                            sx={{
                                                display: 'flex',
                                                flexDirection: 'row',
                                                width: '50%',
                                                justifyContent: 'space-between',
                                            }}
                                        >
                                            <TimePicker
                                                label={t('createCourse.slides.1.form.startTime')}
                                                value={value.startTime}
                                                ampm={false}
                                                onChange={(v) =>
                                                    setFieldValue(`activities.${idx}.startTime`, v)
                                                }
                                                renderInput={({ name: _name, ...params }) => (
                                                    <SlideFormInputField
                                                        name={`activities.${idx}.startTime`}
                                                        textFieldProps={{
                                                            sx: { width: '230px' },
                                                            ...params,
                                                        }}
                                                    />
                                                )}
                                            />
                                            <TimePicker
                                                label={t('createCourse.slides.1.form.endTime')}
                                                value={value.endTime}
                                                ampm={false}
                                                onChange={(v) =>
                                                    setFieldValue(`activities.${idx}.endTime`, v)
                                                }
                                                renderInput={({ name: _name, ...params }) => (
                                                    <SlideFormInputField
                                                        name={`activities.${idx}.endTime`}
                                                        textFieldProps={{
                                                            sx: { width: '230px' },
                                                            ...params,
                                                        }}
                                                    />
                                                )}
                                            />
                                        </Box>

                                        <AutocompleteWithChips
                                            labelPath="createCourse.slides.1.form.lessonModules"
                                            name={`activities.${idx}.lessonModules`}
                                            choosenOptions={value.lessonModules}
                                            options={availableLessonModules}
                                            optionToString={({ name }) => name}
                                            setFieldValue={setFieldValue}
                                        />

                                        <AutocompleteWithChips
                                            labelPath="createCourse.slides.1.form.generalModules"
                                            name={`activities.${idx}.generalModules`}
                                            choosenOptions={value.generalModules}
                                            options={availableGeneralModules}
                                            optionToString={({ name }) => name}
                                            setFieldValue={setFieldValue}
                                        />

                                        <SlideFormInputField
                                            wrapperSx={{ width: '50%' }}
                                            name={`activities.${idx}.instructions`}
                                            type="text"
                                            textFieldProps={{ fullWidth: true, multiline: true }}
                                            labelPath="createCourse.slides.1.form.instructions"
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
