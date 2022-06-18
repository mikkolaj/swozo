import UploadIcon from '@mui/icons-material/Upload';
import { Box, Button, Checkbox, Divider, FormControlLabel, MenuItem, Typography } from '@mui/material';
import { SlideProps } from 'common/SlideForm/SlideForm';
import { SlideFormInputField } from 'common/SlideForm/SlideFormInputField';
import { SlideFormSelectField } from 'common/SlideForm/SlideFormSelectField';
import { Form, Formik } from 'formik';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { mockServices } from 'utils/mocks';

type ModuleValues = {
    name: string;
    subject: string;
    description: string;
    service: string;
    serviceFile: string;
    instructions: string;
    isPublic: boolean;
};

export const GeneralInfoForm = ({
    formRef,
    initialValues,
    setValues: setModuleValues,
}: SlideProps<ModuleValues>) => {
    const [services] = useState(mockServices);
    const { t } = useTranslation();

    return (
        <Formik
            innerRef={formRef}
            initialValues={initialValues}
            validateOnChange={false}
            onSubmit={setModuleValues}
        >
            {({ values, setValues, handleChange }) => (
                <Form>
                    <SlideFormInputField
                        name="name"
                        textFieldProps={{ fullWidth: true }}
                        wrapperSx={{ width: '50%' }}
                        type="text"
                        labelPath="createModule.slides.0.form.name"
                    />
                    <SlideFormInputField
                        name="subject"
                        type="text"
                        labelPath="createModule.slides.0.form.subject"
                    />
                    <SlideFormInputField
                        wrapperSx={{ width: '50%' }}
                        name="description"
                        type="text"
                        textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                        labelPath="createModule.slides.0.form.description"
                    />
                    <SlideFormSelectField name="service" labelPath="createModule.slides.0.form.service">
                        {services.map((service, idx) => (
                            <MenuItem key={idx} value={service}>
                                {service}
                            </MenuItem>
                        ))}
                    </SlideFormSelectField>

                    <Divider sx={{ width: '75%', mt: 2 }} />
                    {/* TODO this should probably be dynamic from server */}
                    <Typography sx={{ mt: 0 }} variant="subtitle1">
                        Konfiguracja serwisu
                    </Typography>
                    <Box
                        sx={{
                            ml: 2,
                            mt: 2,
                            display: 'flex',
                            flexDirection: 'row',
                            alignItems: 'center',
                        }}
                    >
                        <SlideFormInputField
                            name="serviceFile"
                            wrapperSx={{ mt: 0 }}
                            type="text"
                            labelText="Startowy notebook"
                            textFieldProps={{ inputProps: { readOnly: true } }}
                        />
                        <Button
                            sx={{ ml: 2, height: 56 }}
                            endIcon={<UploadIcon />}
                            variant="contained"
                            component="label"
                        >
                            Wybierz plik
                            <input
                                type="file"
                                hidden
                                onChange={(v) => {
                                    console.log(values);
                                    setValues({
                                        ...values,
                                        serviceFile: v.target.files?.item(0)?.name ?? '',
                                    });
                                }}
                            />
                        </Button>
                    </Box>

                    <Divider sx={{ width: '75%', mt: 2 }} />

                    <SlideFormInputField
                        wrapperSx={{ width: '50%' }}
                        name="instructions"
                        type="text"
                        textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                        labelPath="createModule.slides.0.form.instructions"
                    />
                    <FormControlLabel
                        sx={{ mt: 2 }}
                        control={<Checkbox value={values.isPublic} />}
                        label={t('createModule.slides.0.form.public')}
                        name="isPublic"
                        onChange={handleChange}
                    ></FormControlLabel>
                </Form>
            )}
        </Formik>
    );
};
