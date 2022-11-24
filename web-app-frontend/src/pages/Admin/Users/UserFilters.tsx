import { Box, MenuItem } from '@mui/material';
import { AuthDetailsDtoRolesEnum } from 'api';
import { FormInputField } from 'common/Input/FormInputField';
import { FormSelectField } from 'common/Input/FormSelectField';
import { ResourceFilters } from 'common/Input/ResourceFilters';
import { stylesRow } from 'common/styles';
import { useTranslation } from 'react-i18next';
import { ANY_OPTION } from 'utils/types';

export type UserFiltersData = {
    role: AuthDetailsDtoRolesEnum | ANY_OPTION;
    minCreationYear: number;
    maxCreationYear: number;
};

type Props = {
    initialValues: UserFiltersData;
    onModificationApplied: (filters: UserFiltersData) => void;
};

export const UserFilters = ({ initialValues, onModificationApplied }: Props) => {
    const { t } = useTranslation();
    return (
        <ResourceFilters<UserFiltersData>
            initialValues={initialValues}
            onModificationApplied={onModificationApplied}
        >
            <Box sx={{ ...stylesRow }}>
                <FormSelectField name={'role'} i18nLabel="admin.users.filters.roles">
                    <MenuItem value={'any'}>{t('admin.users.filters.any')}</MenuItem>
                    {Object.values(AuthDetailsDtoRolesEnum).map((role) => (
                        <MenuItem key={role} value={role}>
                            {t(`role.${role}`)}
                        </MenuItem>
                    ))}
                </FormSelectField>
                <FormInputField
                    wrapperSx={{ ml: 2 }}
                    name={'minCreationYear'}
                    type="number"
                    i18nLabel="admin.users.filters.minCreationYear"
                />
                <FormInputField
                    wrapperSx={{ ml: 2 }}
                    name={'maxCreationYear'}
                    type="number"
                    i18nLabel="admin.users.filters.maxCreationYear"
                />
            </Box>
        </ResourceFilters>
    );
};
