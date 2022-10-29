import { Box, Button, Card, CardContent, Divider, Grid, Modal, Typography } from '@mui/material';
import { CreateSandboxEnvironmentRequest, ServiceModuleSandboxDto, ServiceModuleSummaryDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { Form, Formik } from 'formik';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation } from 'react-query';
import { SandboxForm } from './SandboxForm';
import { SandboxResultInfo } from './SandboxResultInfo';

type Props = {
    serviceModule: ServiceModuleSummaryDto;
    open: boolean;
    onClose: () => void;
};

const initialValues: CreateSandboxEnvironmentRequest = {
    studentCount: 1,
    resultsValidForMinutes: 15,
    validForMinutes: 15,
};

export const SandboxModal = ({ open, serviceModule, onClose }: Props) => {
    const { t } = useTranslation();
    const [sanboxInfoResult, setSandboxInfoResult] = useState<ServiceModuleSandboxDto>();
    const { pushApiError } = useApiErrorHandling({});

    const createSanboxMutation = useMutation(
        (values: CreateSandboxEnvironmentRequest) =>
            getApis().sandboxApi.createServiceModuleTestingEnvironment({
                serviceModuleId: serviceModule.id,
                createSandboxEnvironmentRequest: values,
            }),
        {
            onSuccess: (res) => {
                setSandboxInfoResult(res);
            },
            onError: pushApiError,
        }
    );

    return (
        <Modal
            open={open}
            onClose={() => (createSanboxMutation.isIdle ? onClose() : undefined)}
            sx={{ width: '50%', margin: 'auto' }}
        >
            <AbsolutelyCentered>
                <Card
                    sx={{
                        borderRadius: 5,
                        border: 'none',
                        boxShadow: 3,
                    }}
                >
                    <CardContent
                        sx={{
                            minHeight: '500px',
                            maxHeight: '80vh',
                            p: 0,
                            overflowY: 'scroll',
                            '::-webkit-scrollbar': {
                                display: 'none',
                            },
                        }}
                    >
                        <Box sx={{ mt: 1, mb: 2 }}>
                            <Typography sx={{ paddingX: 2 }} component="h1" variant="h6" gutterBottom>
                                {t('moduleSandbox.modal.title', { name: serviceModule.name })}
                            </Typography>
                            <Divider />
                            <Formik
                                initialValues={initialValues}
                                onSubmit={(values) => createSanboxMutation.mutate(values)}
                            >
                                {({ values }) => (
                                    <Form>
                                        <Box sx={{ p: 2 }}>
                                            {sanboxInfoResult && (
                                                <SandboxResultInfo info={sanboxInfoResult} />
                                            )}
                                            <Box>
                                                <Typography variant="h5" gutterBottom>
                                                    {t('moduleSandbox.modal.setup.instruction')}
                                                </Typography>
                                            </Box>
                                            <Box>
                                                <Typography>
                                                    {t('moduleSandbox.modal.setup.mainInfo', {
                                                        studentCount: values.studentCount,
                                                    })}
                                                </Typography>
                                            </Box>
                                            <Box sx={{ mt: 2 }}>
                                                <Typography>
                                                    {t('moduleSandbox.modal.setup.moreInfo', {
                                                        validForMinutes: values.validForMinutes,
                                                        resultsValidForMinutes: values.resultsValidForMinutes,
                                                    })}
                                                </Typography>
                                            </Box>
                                            <Box sx={{ mt: 2 }}>
                                                <Typography>
                                                    {t('moduleSandbox.modal.setup.limit')}
                                                </Typography>
                                            </Box>
                                        </Box>
                                        {!sanboxInfoResult && (
                                            <SandboxForm buttonDisabled={createSanboxMutation.isLoading} />
                                        )}
                                    </Form>
                                )}
                            </Formik>
                        </Box>
                    </CardContent>
                    <Grid container sx={{ justifyContent: 'flex-end', p: 1 }}>
                        <Button onClick={onClose}>{t('course.activity.linksInfo.closeButton')}</Button>
                    </Grid>
                </Card>
            </AbsolutelyCentered>
        </Modal>
    );
};
