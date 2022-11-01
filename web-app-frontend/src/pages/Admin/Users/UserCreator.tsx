import { Box, Button, Divider, Paper, Typography } from '@mui/material';
import { CreateUserRequest, CreateUserRequestRolesEnum } from 'api';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { AutocompleteWithChips } from 'common/Input/AutocompleteWithChips';
import { FileInputButton } from 'common/Input/FileInputButton';
import { FormInputField } from 'common/Input/FormInputField';
import { FORM_INPUT_WIDTH_NUM, stylesColumn, stylesRow } from 'common/styles';
import { Form, Formik, FormikProps } from 'formik';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-toastify';
import { handleFlatFormError } from 'utils/util';
import { initialUserValues, updateUserCacheAfterCreation } from './utils';

export const UserCreator = () => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();
    const formRef = useRef<FormikProps<CreateUserRequest>>(null);

    const { pushApiError } = useApiErrorHandling({});

    const createUserMutation = useMutation(
        (createUserRequest: CreateUserRequest) => getApis().userApi.createUser({ createUserRequest }),
        {
            onSuccess: (userData) => {
                updateUserCacheAfterCreation([userData], queryClient);
                toast.success(t('toast.userCreated'));
                formRef.current?.setValues(initialUserValues());
            },
            onError: (err: ApiError) => {
                handleFlatFormError(t, formRef.current, err, 'admin.users.create.form.error', pushApiError);
            },
        }
    );

    return (
        <Box sx={{ ...stylesColumn }}>
            <Box sx={{ ml: 2 }}>
                <Typography gutterBottom variant="h5">
                    {t('admin.users.create.form.info')}
                </Typography>
            </Box>
            <Box sx={{ ...stylesRow, justifyContent: 'center' }}>
                <Formik
                    innerRef={formRef}
                    initialValues={initialUserValues()}
                    onSubmit={(values) => createUserMutation.mutate(values)}
                >
                    {({ values, setFieldValue }) => (
                        <Form>
                            <Box sx={{ width: `${2 * FORM_INPUT_WIDTH_NUM + 20}px`, height: '280px' }}>
                                <Box sx={{ ...stylesRow }}>
                                    <FormInputField
                                        name={'name'}
                                        type="text"
                                        i18nLabel="admin.users.create.form.name"
                                        wrapperSx={{ mr: '20px' }}
                                    />
                                    <FormInputField
                                        name={'surname'}
                                        type="text"
                                        i18nLabel="admin.users.create.form.surname"
                                    />
                                </Box>
                                <FormInputField
                                    name={'email'}
                                    type="text"
                                    i18nLabel="admin.users.create.form.email"
                                    textFieldProps={{ fullWidth: true }}
                                />
                                <AutocompleteWithChips
                                    labelPath="admin.users.create.form.roles"
                                    name={'roles'}
                                    chosenOptions={values.roles}
                                    options={Object.values(CreateUserRequestRolesEnum)}
                                    optionToString={(role) => t(`role.${role}`)}
                                    setFieldValue={setFieldValue}
                                    required={false}
                                    fullWidthChips
                                />
                            </Box>
                            <Box sx={{ ...stylesRow, justifyContent: 'center' }}>
                                <Button
                                    disabled={createUserMutation.isLoading}
                                    type="submit"
                                    variant="contained"
                                    sx={{ p: 2, width: '200px' }}
                                >
                                    {t('admin.users.create.form.createButton')}
                                </Button>
                            </Box>
                        </Form>
                    )}
                </Formik>
            </Box>

            <Divider sx={{ my: 4 }} />

            <Box>
                <Box sx={{ ml: 2 }}>
                    <Typography variant="h5">Inne opcje</Typography>
                </Box>
                <Paper sx={{ p: 2, boxShadow: 3, mt: 2 }}>
                    <Box sx={{ mb: 2 }}>
                        <Typography variant="h6">{t('admin.users.create.import.info')}</Typography>
                        <Typography>{t('admin.users.create.import.format')}</Typography>
                        <Typography>{t('admin.users.create.import.availableRoles')}</Typography>
                    </Box>
                    <Typography>{t('admin.users.create.import.exampleInfo')}</Typography>
                    <Typography>{t('admin.users.create.import.example1')}</Typography>
                    <Typography>{t('admin.users.create.import.example2')}</Typography>
                    <Box sx={{ ...stylesRow, justifyContent: 'center', mt: 2 }}>
                        <FileInputButton
                            disabled={false}
                            sx={{ minWidth: 200 }}
                            allowedExtensions={['csv']}
                            text={t('admin.users.create.import.button')}
                            onFileSelected={() => undefined}
                        />
                    </Box>
                </Paper>
            </Box>
        </Box>
    );
};
