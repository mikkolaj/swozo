import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { Box, Button, Typography } from '@mui/material';
import { UserAdminSummaryDto } from 'api';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { SearchBar } from 'common/Styled/SearchBar';
import { stylesColumn, stylesRow, stylesRowCenteredVertical } from 'common/styles';
import { useFilters } from 'hooks/useFilters';
import _ from 'lodash';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { formatName } from 'utils/util';
import { UserFilters } from './UserFilters';
import { filterUsers } from './utils';

type Props = {
    users: UserAdminSummaryDto[];
};

export const UserList = ({ users }: Props) => {
    const [showFilters, setShowFilters] = useState(false);
    const { t } = useTranslation();
    const {
        filteredResources: filteredUsers,
        filters,
        searchPhrase,
        setFilters,
        setSearchPhrase,
    } = useFilters(
        { role: 'any', minCreationYear: 2010, maxCreationYear: new Date().getFullYear() },
        users,
        filterUsers
    );
    return (
        <Box sx={{ ...stylesColumn, width: '100%' }}>
            <Box sx={{ mb: 4 }}>
                <Box sx={{ ...stylesRowCenteredVertical, position: 'relative' }}>
                    <Box sx={{ width: '500px', margin: 'auto' }}>
                        <SearchBar
                            sx={{ ml: 3, width: '100%' }}
                            placeholder={t('admin.users.searchBoxPlaceholder')}
                            value={searchPhrase}
                            onChange={(e) => setSearchPhrase(e.target.value)}
                        />
                    </Box>
                    <Box sx={{ position: 'absolute', right: 0 }}>
                        <ButtonWithIconAndText
                            Icon={showFilters ? ExpandLessIcon : ExpandMoreIcon}
                            textI18n="admin.users.filters.button"
                            onClick={() => setShowFilters((show) => !show)}
                        />
                    </Box>
                </Box>
                {showFilters && (
                    <Box sx={{ ...stylesRow, justifyContent: 'center', mt: 2 }}>
                        <UserFilters initialValues={filters} onModificationApplied={setFilters} />
                    </Box>
                )}
            </Box>
            <StackedList
                /* eslint-disable react/jsx-key */
                header={
                    <StackedListHeader
                        proportions={[3, 3, 2]}
                        items={['name', 'email', 'roles'].map((label) => (
                            <Typography variant="body1" color="GrayText">
                                {t(`admin.users.list.headers.${label}`)}
                            </Typography>
                        ))}
                    />
                }
                content={
                    <StackedListContent
                        proportions={[3, 3, 4, 2]}
                        emptyItemsComponent={
                            <Box sx={{ ...stylesColumn, pt: 4, alignItems: 'center' }}>
                                <Typography variant="h4">{t('admin.users.list.empty')}</Typography>
                            </Box>
                        }
                        items={filteredUsers}
                        itemKeyExtractor={({ id }) => id}
                        itemRenderer={({ id, name, surname, email, roles }) => [
                            <Typography variant="body1">{formatName(name, surname)}</Typography>,
                            <Typography variant="body1">{email}</Typography>,
                            <Typography variant="body1">
                                {_.join(
                                    roles.map((role) => t(`role.${role}`)),
                                    ', '
                                )}
                            </Typography>,
                            <Box sx={{ ml: 'auto' }}>
                                <Button variant="text">
                                    <Link
                                        target="_blank"
                                        rel="noopener"
                                        to={PageRoutes.AdminUserDetails(id)}
                                        style={{ textDecoration: 'none', color: 'inherit' }}
                                    >
                                        {t('admin.users.list.details')}
                                    </Link>
                                </Button>
                            </Box>,
                        ]}
                    />
                }
                /* eslint-enable react/jsx-key */
            />
        </Box>
    );
};
