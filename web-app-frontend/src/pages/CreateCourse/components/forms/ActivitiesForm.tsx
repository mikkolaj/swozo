import { Box, Typography } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { AutocompleteWithChips } from 'common/Input/AutocompleteWithChips';
import { FormDatePicker } from 'common/Input/FormDatePicker';
import { FormInputField } from 'common/Input/FormInputField';
import { FormTimePicker } from 'common/Input/FormTimePicker';
import { RichTextEditor } from 'common/Input/RichTextEditor';
import { SlideProps } from 'common/SlideForm/util';
import { Bar } from 'common/Styled/Bar';
import { stylesRowWithSpaceBetweenItems } from 'common/styles';
import { FieldArray } from 'formik';
import { ActivityValues } from 'pages/CreateCourse/util';
import { useTranslation } from 'react-i18next';
import { ValidationSchema } from 'utils/types';
import * as Yup from 'yup';

type Props = SlideProps & {
    values: { activities: ActivityValues[] };
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setFieldValue: (name: string, value: any) => void;
    availableLessonModules: ServiceModuleSummaryDto[];
    availableGeneralModules: ServiceModuleSummaryDto[];
};

export const activityValidationSchema: ValidationSchema<ActivityValues> = {
    name: Yup.string().max(1000, 'e1').required('e2'),
};

export const ActivitiesForm = ({
    values,
    setFieldValue,
    nameBuilder,
    availableLessonModules,
    availableGeneralModules,
}: Props) => {
    const { t } = useTranslation();

    return (
        <>
            <FieldArray
                name={nameBuilder('activities')}
                render={(_) => (
                    <Box>
                        {values.activities.map((value, idx) => (
                            <Box key={idx} sx={{ mt: idx > 0 ? 8 : 0 }}>
                                <Typography sx={{ ml: -4 }} variant="h5" component="div" gutterBottom>
                                    {t('createCourse.slides.1.form.activityHeader', {
                                        number: idx + 1,
                                    })}
                                </Typography>

                                <FormInputField
                                    name={nameBuilder(`activities.${idx}.name`)}
                                    textFieldProps={{ fullWidth: true }}
                                    wrapperSx={{ width: '50%' }}
                                    type="text"
                                    i18nLabel="createCourse.slides.1.form.name"
                                />

                                <FormInputField
                                    wrapperSx={{ width: '50%' }}
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

                                <Box sx={{ ...stylesRowWithSpaceBetweenItems, width: '50%' }}>
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

                                <AutocompleteWithChips
                                    labelPath="createCourse.slides.1.form.lessonModules"
                                    name={nameBuilder(`activities.${idx}.lessonModules`)}
                                    chosenOptions={value.lessonModules}
                                    options={availableLessonModules}
                                    optionToString={({ name }) => name}
                                    setFieldValue={setFieldValue}
                                />

                                <AutocompleteWithChips
                                    labelPath="createCourse.slides.1.form.generalModules"
                                    name={nameBuilder(`activities.${idx}.generalModules`)}
                                    chosenOptions={value.generalModules}
                                    options={availableGeneralModules}
                                    optionToString={({ name }) => name}
                                    setFieldValue={setFieldValue}
                                />

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
