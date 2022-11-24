import { Box, Button, Container, Grid } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesRowCenteredVertical, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useQuery } from 'react-query';
import { UserCreator } from './Users/UserCreator';
import { UserList } from './Users/UserList';

type Tab = 'list' | 'create';

export const AdminPanel = () => {
    const { t } = useTranslation();
    const [tab, setTab] = useState<Tab>('list');

    const { isApiError, errorHandler, consumeErrorAction, isApiErrorSet, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: users } = useErrorHandledQuery(
        ['users'],
        () => getApis().userApi.getUsers(),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    // prefetch
    useQuery(['vms'], () => getApis().vmApi.getAllVms());

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
                {tab === 'list' && <UserList users={users ?? []} />}
                {tab === 'create' && <UserCreator />}
            </Container>
        </PageContainer>
    );
};
