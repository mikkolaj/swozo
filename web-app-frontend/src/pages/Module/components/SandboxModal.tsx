import { Box, Button, Card, CardContent, Divider, Grid, Modal, Typography } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { FormInputField } from 'common/Input/FormInputField';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { stylesRowCenteredVertical, stylesRowWithSpaceBetweenItems } from 'common/styles';
import { Form, Formik } from 'formik';
import { useTranslation } from 'react-i18next';

type Props = {
    serviceModule: ServiceModuleSummaryDto;
    open: boolean;
    onClose: () => void;
};

type FormValues = {
    studentCount: number;
    validForMinutes: number;
    resultsValidForMinutes: number;
};

const initialValues: FormValues = {
    studentCount: 1,
    resultsValidForMinutes: 15,
    validForMinutes: 15,
};

export const SandboxModal = ({ open, serviceModule, onClose }: Props) => {
    const { t } = useTranslation();

    return (
        <Modal open={open} onClose={onClose} sx={{ width: '50%', margin: 'auto' }}>
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
                            <Formik initialValues={initialValues} onSubmit={(values) => console.log(values)}>
                                {({ values }) => (
                                    <Form>
                                        <Box sx={{ p: 2 }}>
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
                                        <Box sx={{ ...stylesRowWithSpaceBetweenItems, px: 12 }}>
                                            <FormInputField
                                                name="studentCount"
                                                type="number"
                                                i18nLabel="moduleSandbox.modal.setup.form.studentCount"
                                            />
                                            <FormInputField
                                                name="validForMinutes"
                                                type="number"
                                                i18nLabel="moduleSandbox.modal.setup.form.validForMinutes"
                                            />
                                            <FormInputField
                                                name="resultsValidForMinutes"
                                                type="number"
                                                i18nLabel="moduleSandbox.modal.setup.form.resultsValidForMinutes"
                                            />
                                        </Box>
                                        <Box
                                            sx={{
                                                ...stylesRowCenteredVertical,
                                                justifyContent: 'center',
                                                mt: 4,
                                            }}
                                        >
                                            <Button
                                                type="submit"
                                                fullWidth
                                                variant="contained"
                                                sx={{ width: '300px' }}
                                            >
                                                {t('moduleSandbox.modal.setup.form.createButton')}
                                            </Button>
                                        </Box>
                                    </Form>
                                )}
                            </Formik>
                        </Box>
                        {/* <Box sx={{ height: '5000px' }}></Box> */}
                    </CardContent>
                    <Grid container sx={{ justifyContent: 'flex-end', p: 1 }}>
                        <Button onClick={onClose}>{t('course.activity.linksInfo.closeButton')}</Button>
                    </Grid>
                </Card>
            </AbsolutelyCentered>
        </Modal>
    );
};
