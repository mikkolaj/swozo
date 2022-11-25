import { Box, Button, Typography } from '@mui/material';
import { CreatePolicyRequest, PolicyDtoPolicyTypeEnum, UserAdminDetailsDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { FormInputField } from 'common/Input/FormInputField';
import { FORM_INPUT_WIDTH_NUM, stylesRow } from 'common/styles';
import { Form, Formik } from 'formik';
import { StyledReadonlyField } from 'pages/Module/components/ServiceModuleGeneralInfo';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useDispatch } from 'react-redux';
import { toast } from 'react-toastify';
import { triggerError } from 'services/features/error/errorSlice';
import { toPolicyFormValues, toPolicyUpdateRequest, updateUserDetailsCache } from '../utils';

type Props = {
    user: UserAdminDetailsDto;
    editMode: boolean;
};

const INPUT_WIDTH = 2 * FORM_INPUT_WIDTH_NUM + 20;

export const UserPoliciesView = ({ user, editMode }: Props) => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();
    const dispatch = useDispatch();

    const updatePoliciesMutation = useMutation(
        (createPolicyRequest: CreatePolicyRequest[]) =>
            getApis().policyApi.overwriteAllTeacherPolicies({ userId: user.id, createPolicyRequest }),
        {
            onSuccess: (newPolicies) => {
                updateUserDetailsCache(
                    {
                        ...user,
                        userPolicies: newPolicies,
                    },
                    queryClient
                );
                toast.success(t('toast.policiesUpdated'));
            },
            onError: () => {
                dispatch(triggerError({ message: t('error.tryAgain') }));
            },
        }
    );

    return (
        <Box>
            {!editMode ? (
                <Box>
                    {user.userPolicies.map(({ id, policyType, value }) => (
                        <StyledReadonlyField
                            key={id}
                            value={`${value}`}
                            textFieldProps={{ sx: { width: INPUT_WIDTH, mt: 1 } }}
                            labelText={t(`admin.userDetails.policies.${policyType}`)}
                        />
                    ))}
                    {user.userPolicies.length === 0 && (
                        <Box sx={{ ...stylesRow, justifyContent: 'center' }}>
                            <Typography sx={{ ml: 2, mb: 2 }} variant="h5">
                                {t('admin.userDetails.policies.empty')}
                            </Typography>
                        </Box>
                    )}
                </Box>
            ) : (
                <Box>
                    <Formik
                        initialValues={toPolicyFormValues(user.userPolicies)}
                        onSubmit={(values) =>
                            updatePoliciesMutation.mutate(toPolicyUpdateRequest(user.id, values))
                        }
                    >
                        {() => (
                            <Form>
                                {Object.values(PolicyDtoPolicyTypeEnum).map((policyType) => (
                                    <FormInputField
                                        key={policyType}
                                        name={policyType}
                                        type="number"
                                        textFieldProps={{
                                            sx: { width: INPUT_WIDTH, mt: 1 },
                                        }}
                                        labelText={t(`admin.userDetails.policies.${policyType}`)}
                                    />
                                ))}
                                <Button type="submit" disabled={updatePoliciesMutation.isLoading}>
                                    {t('admin.userDetails.policies.save')}
                                </Button>
                            </Form>
                        )}
                    </Formik>
                </Box>
            )}
        </Box>
    );
};
