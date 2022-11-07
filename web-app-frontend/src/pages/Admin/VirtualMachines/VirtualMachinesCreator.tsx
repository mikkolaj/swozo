import { Box, Button, Typography } from '@mui/material';
import { CreateVmRequest, VmDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { FormInputField } from 'common/Input/FormInputField';
import { RichTextEditor } from 'common/Input/RichTextEditor';
import { stylesRowCenteredHorizontal } from 'common/styles';
import { Form, Formik } from 'formik';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useDispatch } from 'react-redux';
import { toast } from 'react-toastify';
import { triggerError } from 'services/features/error/errorSlice';
import { initialVmValues } from './utils';

export const VirtualMachinesCreator = () => {
    const { t } = useTranslation();
    const dispatch = useDispatch();
    const queryClient = useQueryClient();

    const createVmMutation = useMutation(
        (createVmRequest: CreateVmRequest) => getApis().vmApi.addVm({ createVmRequest }),
        {
            onSuccess: (vm) => {
                queryClient.setQueryData(['vms'], (prevVms: VmDto[] = []) => [...prevVms, vm]);
                toast.success(t('toast.vmCreated'));
            },
            onError: () => {
                dispatch(triggerError({ message: t('error.tryAgain') }));
            },
        }
    );

    return (
        <Box sx={{}}>
            <Formik initialValues={initialVmValues()} onSubmit={(values) => createVmMutation.mutate(values)}>
                {({ values, setFieldValue }) => (
                    <Form>
                        <Box sx={{ width: '50%', margin: 'auto' }}>
                            <FormInputField
                                name={'name'}
                                type="text"
                                textFieldProps={{ fullWidth: true }}
                                labelText={t('admin.vms.create.form.name')}
                            />
                            <FormInputField
                                name={'vcpu'}
                                type="number"
                                textFieldProps={{ fullWidth: true }}
                                labelText={t('admin.vms.create.form.vcpu')}
                            />
                            <FormInputField
                                name={'ramGB'}
                                type="number"
                                textFieldProps={{ fullWidth: true }}
                                labelText={t('admin.vms.create.form.ramGB')}
                            />
                            <FormInputField
                                name={'bandwidthMbps'}
                                type="number"
                                textFieldProps={{ fullWidth: true }}
                                labelText={t('admin.vms.create.form.bandwidthMbps')}
                            />
                            <Typography sx={{ mt: 2 }} variant="subtitle1">
                                {t('admin.vms.create.form.description')}
                            </Typography>
                            <RichTextEditor
                                wrapperSx={{ mt: 1 }}
                                name="descriptionHtml"
                                value={values.descriptionHtml}
                                setFieldValue={setFieldValue}
                            />
                            <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mt: 3 }}>
                                <Button
                                    type="submit"
                                    variant="contained"
                                    sx={{ p: 2 }}
                                    disabled={createVmMutation.isLoading}
                                >
                                    {t('admin.vms.create.form.submit')}
                                </Button>
                            </Box>
                        </Box>
                    </Form>
                )}
            </Formik>
        </Box>
    );
};
