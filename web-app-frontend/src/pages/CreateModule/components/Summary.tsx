import { Box, Divider, Paper, Typography } from '@mui/material';
import { ServiceConfig } from 'api';
import { stylesColumnCenteredVertical, stylesRowCenteredHorizontal } from 'common/styles';
import { useTranslation } from 'react-i18next';
import { FormValues, MODULE_INFO_SLIDE, MODULE_SPECS_SLIDE } from '../util/types';

type Props = {
    formValues: FormValues;
    editMode: boolean;
    supportedServices: ServiceConfig[];
};

export const Summary = ({ formValues, editMode, supportedServices }: Props) => {
    const { t } = useTranslation();
    const moduleInfo = formValues[MODULE_INFO_SLIDE];
    const mdaInfo = formValues[MODULE_SPECS_SLIDE];

    return (
        <Box>
            <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mb: 4 }}>
                <Typography sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }} variant="h3">
                    {moduleInfo.name}
                </Typography>
            </Box>
            <Box sx={{ ...stylesColumnCenteredVertical }}>
                <Box>
                    <Paper sx={{ p: 2, boxShadow: 3 }}>
                        <Typography variant="h5">
                            {t('createModule.slides.2.service', {
                                serviceName: supportedServices.find(
                                    (service) => service.serviceName === formValues[MODULE_INFO_SLIDE].service
                                )?.displayName,
                            })}
                        </Typography>
                    </Paper>
                </Box>

                <Box sx={{ mt: 2 }}>
                    <Paper sx={{ p: 2, boxShadow: 3 }}>
                        <Box>
                            <Typography variant="h5">
                                {t(
                                    `createModule.slides.2.accessibility.${
                                        moduleInfo.isPublic ? 'public' : 'private'
                                    }.label`
                                )}
                            </Typography>
                            <Typography variant="subtitle1">
                                {t(
                                    `createModule.slides.2.accessibility.${
                                        moduleInfo.isPublic ? 'public' : 'private'
                                    }.info`
                                )}
                            </Typography>
                        </Box>
                    </Paper>
                </Box>

                <Box sx={{ mt: 2 }}>
                    <Paper sx={{ p: 2, boxShadow: 3 }}>
                        <Box>
                            <Typography variant="h5">
                                {t(`createModule.slides.2.isolation.${mdaInfo.isolationMode}.label`)}
                            </Typography>
                            <Typography variant="subtitle1">
                                {t(`createModule.slides.2.isolation.${mdaInfo.isolationMode}.info`)}
                            </Typography>
                        </Box>
                    </Paper>
                </Box>

                <Divider sx={{ my: 4 }} />

                <Box>
                    <Box sx={{ mb: 1 }}>
                        <Typography variant="h5">{t('createModule.slides.2.disclaimer.title')}</Typography>
                    </Box>
                    <Box>
                        {[...t('createModule.slides.2.disclaimer.lines', { returnObjects: true })].map(
                            (line, idx) => (
                                <Typography key={idx} variant="subtitle1">
                                    {typeof line === 'object'
                                        ? line['editMode'] === editMode
                                            ? line['text']
                                            : ''
                                        : line}
                                </Typography>
                            )
                        )}
                    </Box>
                </Box>
            </Box>
        </Box>
    );
};
