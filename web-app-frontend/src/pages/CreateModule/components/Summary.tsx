import { Box, Divider, Paper, Typography } from '@mui/material';
import { stylesColumnCenteredVertical, stylesRowCenteredHorizontal } from 'common/styles';
import _ from 'lodash';
import { useTranslation } from 'react-i18next';
import { ModuleValues } from '../util/types';

type Props = {
    moduleInfo: ModuleValues;
    editMode: boolean;
};

export const Summary = ({ moduleInfo, editMode }: Props) => {
    const { t } = useTranslation();

    return (
        <Box>
            <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mb: 4 }}>
                <Typography sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }} variant="h3">
                    {moduleInfo.name}
                </Typography>
            </Box>
            <Box sx={{ ...stylesColumnCenteredVertical }}>
                <Box>
                    <Paper sx={{ p: 2 }}>
                        <Typography variant="h5">
                            {t('createModule.slides.2.service', {
                                serviceName: _.capitalize(moduleInfo.service),
                            })}
                        </Typography>
                    </Paper>
                </Box>

                <Box sx={{ mt: 2 }}>
                    <Paper sx={{ p: 2 }}>
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
