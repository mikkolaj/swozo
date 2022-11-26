import { Box, Typography } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { FormDatePicker } from 'common/Input/FormDatePicker';
import { FormInputField } from 'common/Input/FormInputField';
import { FormTimePicker } from 'common/Input/FormTimePicker';
import { RichTextEditor } from 'common/Input/RichTextEditor';
import { SlideProps } from 'common/SlideForm/util';
import { Bar } from 'common/Styled/Bar';
import { stylesRowWithSpaceBetweenItems } from 'common/styles';
import dayjs from 'dayjs';
import { FieldArray } from 'formik';
import { TFunction } from 'i18next';
import { ActivityValues } from 'pages/CreateCourse/util';
import { useTranslation } from 'react-i18next';
import { ValidationSchema } from 'utils/types';
import * as Yup from 'yup';
import { ServiceModuleInput } from './ServiceModuleInput';

type Props = SlideProps & {
    values: { activities: ActivityValues[] };
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setFieldValue: (name: string, value: any) => void;
    availableLessonModules: ServiceModuleSummaryDto[];
    availableGeneralModules: ServiceModuleSummaryDto[];
    createMode?: boolean;
    editMode?: boolean;
};

export const activityValidationSchema = (t: TFunction): ValidationSchema<ActivityValues> => ({
    name: Yup.string()
        .max(255, t('commonErrors.validation.tooLong'))
        .required(t('commonErrors.validation.required')),
    description: Yup.string().max(255, t('commonErrors.validation.tooLong')),
    date: Yup.date()
        .min(dayjs().startOf('day').toDate(), t('commonErrors.validation.future'))
        .required(t('commonErrors.validation.required')),
});

export const ActivitiesForm = ({
    values,
    setFieldValue,
    nameBuilder,
    availableLessonModules,
    createMode = true,
}: Props) => {
    const { t } = useTranslation();
    const width = createMode ? '50%' : '55%';

    return (
        <>
            <FieldArray
                name={nameBuilder('activities')}
                render={(_) => (
                    <Box>
                        {values.activities.map((value, idx) => (
                            <Box key={idx} sx={{ mt: idx > 0 ? 8 : 0 }}>
                                {createMode && (
                                    <Typography sx={{ ml: -4 }} variant="h5" component="div" gutterBottom>
                                        {t('createCourse.slides.1.form.activityHeader', {
                                            number: idx + 1,
                                        })}
                                    </Typography>
                                )}

                                <FormInputField
                                    name={nameBuilder(`activities.${idx}.name`)}
                                    textFieldProps={{ fullWidth: true }}
                                    wrapperSx={{ width }}
                                    type="text"
                                    i18nLabel="createCourse.slides.1.form.name"
                                />

                                <FormInputField
                                    wrapperSx={{ width }}
                                    name={nameBuilder(`activities.${idx}.description`)}
                                    type="text"
                                    textFieldProps={{
                                        fullWidth: true,
                                        multiline: true,
                                        required: false,
                                    }}
                                    i18nLabel="createCourse.slides.1.form.description"
                                />

                                <FormDatePicker
                                    name={nameBuilder(`activities.${idx}.date`)}
                                    label={t('createCourse.slides.1.form.date')}
                                    value={value.date}
                                    setFieldValue={setFieldValue}
                                />

                                <Box sx={{ ...stylesRowWithSpaceBetweenItems, width }}>
                                    <FormTimePicker
                                        name={nameBuilder(`activities.${idx}.startTime`)}
                                        label={t('createCourse.slides.1.form.startTime')}
                                        value={value.startTime}
                                        setFieldValue={setFieldValue}
                                    />
                                    <FormTimePicker
                                        name={nameBuilder(`activities.${idx}.endTime`)}
                                        label={t('createCourse.slides.1.form.endTime')}
                                        value={value.endTime}
                                        setFieldValue={setFieldValue}
                                    />
                                </Box>

                                <ServiceModuleInput
                                    slideIndependantName={`activities.${idx}.lessonModules`}
                                    availableModules={availableLessonModules}
                                    labelI18n="createCourse.slides.1.form.lessonModules"
                                    selectBoxLabelI18n="createCourse.slides.1.form.linkConfirmationRequiredLabel"
                                    setFieldValue={setFieldValue}
                                    nameBuilder={nameBuilder}
                                    value={value}
                                    listExtractor={(v) => v.lessonModules}
                                />

                                {/* <ServiceModuleInput
                                    slideIndependantName={`activities.${idx}.generalModules`}
                                    availableModules={availableGeneralModules}
                                    labelI18n="createCourse.slides.1.form.generalModules"
                                    selectBoxLabelI18n="createCourse.slides.1.form.linkConfirmationRequiredLabel"
                                    setFieldValue={setFieldValue}
                                    nameBuilder={nameBuilder}
                                    value={value}
                                    listExtractor={(v) => v.generalModules}
                                /> */}

                                <Typography sx={{ mt: 1 }} variant="subtitle1">
                                    {t('createCourse.slides.1.form.instructions')}
                                </Typography>

                                <RichTextEditor
                                    wrapperSx={{ width: '75%' }}
                                    name={nameBuilder(`activities.${idx}.instructions`)}
                                    value={value.instructions}
                                    setFieldValue={setFieldValue}
                                />
                                {idx < values.activities.length - 1 && <Bar sx={{ mt: 4 }} />}
                            </Box>
                        ))}
                    </Box>
                )}
            ></FieldArray>
        </>
    );
};
