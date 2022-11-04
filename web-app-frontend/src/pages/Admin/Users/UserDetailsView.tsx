import EditIcon from '@mui/icons-material/Edit';
import { Box, Button, Chip, Container, Grid, Typography } from '@mui/material';
import { UserAdminDetailsDtoRolesEnum } from 'api';
import { getApis } from 'api/initialize-apis';
import { AutocompleteWithChips } from 'common/Input/AutocompleteWithChips';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import {
    stylesColumnCenteredVertical,
    stylesRow,
    stylesRowCenteredVertical,
    stylesRowWithItemsAtTheEnd,
} from 'common/styles';
import { Formik } from 'formik';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useRequiredParams } from 'hooks/useRequiredParams';
import _ from 'lodash';
import { StyledReadonlyField } from 'pages/Module/components/ServiceModuleGeneralInfo';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-toastify';
import { formatBytes, formatDate, formatName } from 'utils/util';
import { AttendedCoursesList } from './components/AttendedCoursesList';
import { CreatedCoursesList } from './components/CreatedCoursesList';
import { CreatedServiceModulesList } from './components/CreatedServiceModulesList';

export const UserDetailsView = () => {
    const { t } = useTranslation();
    const [userId] = useRequiredParams(['userId']);
    const queryClient = useQueryClient();
    const [editMode, setEditMode] = useState(false);
    const [selectedRoles, setSelectedRoles] = useState<UserAdminDetailsDtoRolesEnum[]>([]);
    const { pushApiError, removeApiError, isApiErrorSet } = useApiErrorHandling({});

    const { data: user } = useErrorHandledQuery(
        ['users', 'details', userId],
        () => getApis().userApi.getUserDetailsForAdmin({ userId: +userId }),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    const updateRolesMutation = useMutation(
        (roles: UserAdminDetailsDtoRolesEnum[]) =>
            getApis().userApi.setUserRoles({ userId: +userId, requestBody: roles }),
        {
            onSuccess: (result) => {
                toast.success(t('toast.rolesUpdated'));
                queryClient.setQueryData(['users', 'details', userId], result);
            },
        }
    );

    useEffect(() => {
        if (user) {
            setSelectedRoles(user.roles);
        }
    }, [user]);

    if (!user) {
        return <PageContainerWithLoader />;
    }

    return (
        <PageContainer
            header={
                <>
                    <Grid item xs={7}>
                        <Box sx={{ ...stylesRowCenteredVertical }}>
                            <PageHeaderText text={t('admin.userDetails.header')} />
                        </Box>
                    </Grid>
                    <Grid item xs={5} sx={stylesRowWithItemsAtTheEnd}>
                        <ButtonWithIconAndText
                            onClick={() => setEditMode((editMode) => !editMode)}
                            textI18n={`admin.userDetails.edit.${editMode ? 'turnOff' : 'turnOn'}`}
                            Icon={EditIcon}
                        />
                    </Grid>
                </>
            }
        >
            <Container>
                <Box sx={{ ...stylesColumnCenteredVertical }}>
                    <Box sx={{ ...stylesRow }}>
                        <StyledReadonlyField
                            value={formatName(user.name, user.surname)}
                            i18nLabel="admin.userDetails.name"
                        />
                        <StyledReadonlyField value={user.email} i18nLabel="admin.userDetails.email" />
                    </Box>

                    <StyledReadonlyField
                        value={formatDate(user.createdAt)}
                        i18nLabel="admin.userDetails.createdAt"
                    />

                    <StyledReadonlyField
                        value={formatBytes(user.storageUsageBytes)}
                        i18nLabel="admin.userDetails.storage"
                    />

                    {!editMode ? (
                        <Box sx={{ ml: 2 }}>
                            <Typography>{t('admin.userDetails.roles')}</Typography>
                            <Box sx={{ ...stylesRow }}>
                                {user.roles.map((role) => (
                                    <Chip key={role} label={t(`role.${role}`)} />
                                ))}
                            </Box>
                        </Box>
                    ) : (
                        <Formik
                            initialValues={{ roles: selectedRoles }}
                            onSubmit={() => updateRolesMutation.mutate(selectedRoles)}
                        >
                            {({ submitForm }) => (
                                <Box sx={{ ml: 1 }}>
                                    <AutocompleteWithChips
                                        labelPath="admin.userDetails.roles"
                                        name={'roles'}
                                        chosenOptions={selectedRoles}
                                        options={Object.values(UserAdminDetailsDtoRolesEnum)}
                                        optionToString={(role) => t(`role.${role}`)}
                                        setFieldValue={(_, v) => setSelectedRoles(v)}
                                        required={false}
                                        fullWidthChips
                                        componentToRenderRightToFieldInput={
                                            <Button
                                                sx={{ mt: 2, ml: 2 }}
                                                disabled={
                                                    updateRolesMutation.isLoading ||
                                                    (user.roles.length === selectedRoles.length &&
                                                        _.difference(user.roles, selectedRoles).length === 0)
                                                }
                                                onClick={submitForm}
                                            >
                                                {t('admin.userDetails.saveRoles')}
                                            </Button>
                                        }
                                    />
                                </Box>
                            )}
                        </Formik>
                    )}

                    <Box sx={{ my: 4 }} />
                    <AttendedCoursesList user={user} />
                    <Box sx={{ my: 3 }} />
                    <CreatedCoursesList user={user} />
                    <Box sx={{ my: 3 }} />
                    <CreatedServiceModulesList user={user} />
                    <Box sx={{ my: 3 }} />
                </Box>
            </Container>
        </PageContainer>
    );
};
