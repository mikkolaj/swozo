import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { Box, Button, Container, Grid, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { SearchBar } from 'common/Styled/SearchBar';
import {
    stylesColumn,
    stylesRow,
    stylesRowCenteredVertical,
    stylesRowWithItemsAtTheEnd,
} from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useFilters } from 'hooks/useFilters';
import _ from 'lodash';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { formatName } from 'utils/util';
import { UserFilters } from './Users/UserFilters';
import { filterUsers } from './Users/utils';

type Tab = 'list' | 'create';

export const AdminPanel = () => {
    const { t } = useTranslation();
    const [showFilters, setShowFilters] = useState(false);
    const [tab, setTab] = useState<Tab>('list');

    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: users } = useErrorHandledQuery(
        ['users'],
        () => getApis().userApi.getUsers(),
        pushApiError,
        removeApiError
    );

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

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    return (
        <PageContainer
            header={
                <>
                    <Grid item xs={7}>
                        <Box sx={{ ...stylesRowCenteredVertical }}>
                            <PageHeaderText text={t('admin.users.header')} />
                        </Box>
                    </Grid>
                    <Grid item xs={5} sx={stylesRowWithItemsAtTheEnd}>
                        {new Array<Tab>('list', 'create').map((mappedTab) => (
                            <Button
                                key={mappedTab}
                                onClick={() => setTab(mappedTab)}
                                variant={mappedTab === tab ? 'contained' : 'outlined'}
                                sx={{ mr: 0.5, height: '50px' }}
                            >
                                {t(`admin.users.${mappedTab}.button`)}
                            </Button>
                        ))}
                    </Grid>
                </>
            }
        >
            <Container>
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
                                itemRenderer={({ name, surname, email, roles }) => [
                                    <Typography variant="body1">{formatName(name, surname)}</Typography>,
                                    <Typography variant="body1">{email}</Typography>,
                                    <Typography variant="body1">
                                        {_.join(
                                            roles.map((role) => t(`role.${role}`)),
                                            ', '
                                        )}
                                    </Typography>,
                                    <Box sx={{ ml: 'auto' }}>
                                        <Button variant="contained">{t('admin.users.list.details')}</Button>
                                    </Box>,
                                ]}
                            />
                        }
                        /* eslint-enable react/jsx-key */
                    />
                </Box>
            </Container>
        </PageContainer>
    );
};
