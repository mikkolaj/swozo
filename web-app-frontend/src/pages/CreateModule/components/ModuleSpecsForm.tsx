import { Box, MenuItem, Typography } from '@mui/material';
import { ServiceConfig, ServiceConfigIsolationModesEnum } from 'api';
import { FormInputField } from 'common/Input/FormInputField';
import { FormSelectField } from 'common/Input/FormSelectField';
import { SlideProps } from 'common/SlideForm/util';
import { useTranslation } from 'react-i18next';
import { MdaValues } from '../util/types';

type Props = SlideProps & {
    values: MdaValues;
    serviceConfig?: ServiceConfig;
    editMode?: boolean;
};

export const ModuleSpecsForm = ({ nameBuilder, values, serviceConfig, editMode }: Props) => {
    const { t } = useTranslation();

    return (
        <Box>
            <FormSelectField
                name={nameBuilder('isolationMode')}
                i18nLabel="createModule.slides.1.form.isolation.label"
            >
                {serviceConfig?.isolationModes
                    .filter((isolation) => !editMode || isolation === values.isolationMode)
                    .map((isolationMode) => (
                        <MenuItem key={isolationMode} value={isolationMode}>
                            {t(`createModule.slides.1.form.isolation.${isolationMode}`)}
                        </MenuItem>
                    ))}
            </FormSelectField>
            <Box sx={{ width: '60%', mt: 2 }}>
                <Typography gutterBottom variant="h6">
                    {t('createModule.slides.1.form.base.label')}
                </Typography>
                <FormInputField
                    name={nameBuilder('baseVcpu')}
                    textFieldProps={{ fullWidth: true }}
                    type="number"
                    labelText={t('createModule.slides.1.form.base.vcpu')}
                />
                <FormInputField
                    name={nameBuilder('baseRamGB')}
                    textFieldProps={{ fullWidth: true }}
                    type="number"
                    labelText={t('createModule.slides.1.form.base.ram')}
                />
                <FormInputField
                    name={nameBuilder('baseDiskGB')}
                    textFieldProps={{ fullWidth: true }}
                    type="number"
                    labelText={t('createModule.slides.1.form.base.disk')}
                />
                <FormInputField
                    name={nameBuilder('baseBandwidthMbps')}
                    textFieldProps={{ fullWidth: true }}
                    type="number"
                    labelText={t('createModule.slides.1.form.base.bandwidth')}
                />
                {values.isolationMode === ServiceConfigIsolationModesEnum.Shared && (
                    <Box sx={{ mt: 2 }}>
                        <Typography gutterBottom variant="h6">
                            {t('createModule.slides.1.form.scaled.label')}
                        </Typography>
                        <FormInputField
                            name={nameBuilder('sharedServiceModuleMdaDto.usersPerAdditionalCore')}
                            textFieldProps={{ fullWidth: true }}
                            type="number"
                            labelText={t('createModule.slides.1.form.scaled.vcpu')}
                        />
                        <FormInputField
                            name={nameBuilder('sharedServiceModuleMdaDto.usersPerAdditionalRamGb')}
                            textFieldProps={{ fullWidth: true }}
                            type="number"
                            labelText={t('createModule.slides.1.form.scaled.ram')}
                        />
                        <FormInputField
                            name={nameBuilder('sharedServiceModuleMdaDto.usersPerAdditionalDiskGb')}
                            textFieldProps={{ fullWidth: true }}
                            type="number"
                            labelText={t('createModule.slides.1.form.scaled.disk')}
                        />
                        <FormInputField
                            name={nameBuilder('sharedServiceModuleMdaDto.usersPerAdditionalBandwidthGbps')}
                            textFieldProps={{ fullWidth: true }}
                            type="number"
                            labelText={t('createModule.slides.1.form.scaled.bandwidth')}
                        />
                    </Box>
                )}
            </Box>
        </Box>
    );
};
