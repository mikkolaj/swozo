import { Box, Grid } from '@mui/material';
import { ServiceModuleMdaDto } from 'api';
import { toIsolationMode } from 'pages/CreateModule/util/mapping';
import React, { PropsWithChildren } from 'react';
import { useTranslation } from 'react-i18next';
import { StyledReadonlyField } from './ServiceModuleGeneralInfo';

type Props = {
    mda: ServiceModuleMdaDto;
};

export const ServiceModuleMdaInfo = ({ mda }: Props) => {
    const { t } = useTranslation();

    return (
        <Box>
            <Box sx={{ width: '100%', mt: 2 }}>
                <Grid container>
                    <Grid item xs={mda.sharedServiceModuleMdaDto ? 12 : 6}>
                        <StyledReadonlyField
                            textFieldProps={{ fullWidth: true }}
                            i18nLabel="createModule.slides.1.form.isolation.label"
                            value={t(
                                `createModule.slides.1.form.isolation.${toIsolationMode(mda.isIsolated)}`
                            )}
                        />
                    </Grid>
                </Grid>
                <FieldPair showSecondChild={!!mda.sharedServiceModuleMdaDto}>
                    <StyledReadonlyField
                        textFieldProps={{ fullWidth: true }}
                        value={`${mda.baseVcpu}`}
                        labelText={t('createModule.slides.1.form.base.vcpu')}
                    />
                    <StyledReadonlyField
                        textFieldProps={{ fullWidth: true }}
                        value={`${mda.sharedServiceModuleMdaDto?.usersPerAdditionalCore}`}
                        labelText={t('createModule.slides.1.form.scaled.vcpu')}
                    />
                </FieldPair>

                <FieldPair showSecondChild={!!mda.sharedServiceModuleMdaDto}>
                    <StyledReadonlyField
                        textFieldProps={{ fullWidth: true }}
                        value={`${mda.baseRamGB}`}
                        labelText={t('createModule.slides.1.form.base.ram')}
                    />
                    <StyledReadonlyField
                        textFieldProps={{ fullWidth: true }}
                        value={`${mda.sharedServiceModuleMdaDto?.usersPerAdditionalRamGb}`}
                        labelText={t('createModule.slides.1.form.scaled.ram')}
                    />
                </FieldPair>

                <FieldPair showSecondChild={!!mda.sharedServiceModuleMdaDto}>
                    <StyledReadonlyField
                        textFieldProps={{ fullWidth: true }}
                        value={`${mda.baseDiskGB}`}
                        labelText={t('createModule.slides.1.form.base.disk')}
                    />
                    <StyledReadonlyField
                        textFieldProps={{ fullWidth: true }}
                        value={`${mda.sharedServiceModuleMdaDto?.usersPerAdditionalRamGb}`}
                        labelText={t('createModule.slides.1.form.scaled.disk')}
                    />
                </FieldPair>

                <FieldPair showSecondChild={!!mda.sharedServiceModuleMdaDto}>
                    <StyledReadonlyField
                        textFieldProps={{ fullWidth: true }}
                        value={`${mda.baseBandwidthMbps}`}
                        labelText={t('createModule.slides.1.form.base.bandwidth')}
                    />

                    <StyledReadonlyField
                        textFieldProps={{ fullWidth: true }}
                        value={`${mda.sharedServiceModuleMdaDto?.usersPerAdditionalBandwidthGbps}`}
                        labelText={t('createModule.slides.1.form.scaled.bandwidth')}
                    />
                </FieldPair>
            </Box>
        </Box>
    );
};

const FieldPair = ({ showSecondChild, children }: PropsWithChildren<{ showSecondChild: boolean }>) => {
    const childrenArr = React.Children.toArray(children);
    return (
        <Grid container>
            <Grid item xs={6}>
                {childrenArr[0]}
            </Grid>
            <Grid item xs={6}>
                {showSecondChild && childrenArr[1]}
            </Grid>
        </Grid>
    );
};
