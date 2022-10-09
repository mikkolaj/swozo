import { Box, Checkbox, Divider, FormControlLabel, MenuItem, Typography } from '@mui/material';
import { FileInputButton } from 'common/Input/FileInputButton';
import { FormInputField } from 'common/Input/FormInputField';
import { FormSelectField } from 'common/Input/FormSelectField';
import { SlideProps } from 'common/SlideForm/util';
import { stylesRowCenteredVertical } from 'common/styles';
import { ChangeEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { mockServices } from 'utils/mocks';
import { ModuleValues } from '../util';

type Props = SlideProps & {
    values: ModuleValues;
    setValues: (values: ModuleValues) => void;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    handleChange: (e: ChangeEvent<any>) => void;
};

export const ModuleInfoForm = ({ nameBuilder, values, setValues, handleChange }: Props) => {
    const [services] = useState(mockServices);
    const { t } = useTranslation();

    return (
        <>
            <FormInputField
                name={nameBuilder('name')}
                textFieldProps={{ fullWidth: true }}
                wrapperSx={{ width: '50%' }}
                type="text"
                i18nLabel="createModule.slides.0.form.name"
            />
            <FormInputField
                name={nameBuilder('subject')}
                type="text"
                i18nLabel="createModule.slides.0.form.subject"
            />
            <FormInputField
                wrapperSx={{ width: '50%' }}
                name={nameBuilder('description')}
                type="text"
                textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                i18nLabel="createModule.slides.0.form.description"
            />
            <FormSelectField name={nameBuilder('service')} i18nLabel="createModule.slides.0.form.service">
                {services.map((service, idx) => (
                    <MenuItem key={idx} value={service}>
                        {service}
                    </MenuItem>
                ))}
            </FormSelectField>

            <Divider sx={{ width: '75%', mt: 2 }} />
            {/* TODO this should probably be dynamic from server */}
            <Typography sx={{ mt: 0 }} variant="subtitle1">
                Konfiguracja serwisu
            </Typography>
            <Box
                sx={{
                    ...stylesRowCenteredVertical,
                    ml: 2,
                    mt: 2,
                }}
            >
                <FormInputField
                    name={nameBuilder('serviceFile')}
                    wrapperSx={{ mt: 0 }}
                    type="text"
                    labelText="Startowy notebook"
                    textFieldProps={{ inputProps: { readOnly: true } }}
                />
                <FileInputButton
                    text="Wybierz plik"
                    onFilesSelected={(files) => {
                        console.log(files);
                        setValues({
                            ...values,
                            serviceFile: files[0]?.name ?? '',
                        });
                    }}
                />
            </Box>

            <Divider sx={{ width: '75%', mt: 2 }} />

            <FormInputField
                wrapperSx={{ width: '50%' }}
                name={nameBuilder('instructions')}
                type="text"
                textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                i18nLabel="createModule.slides.0.form.instructions"
            />
            <FormControlLabel
                sx={{ mt: 2 }}
                control={<Checkbox value={values.isPublic} />}
                label={t('createModule.slides.0.form.public')}
                name={nameBuilder('isPublic')}
                onChange={handleChange}
            />
        </>
    );
};
