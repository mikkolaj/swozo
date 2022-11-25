import { Box, Container, Grid, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesRow } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useTranslation } from 'react-i18next';
import { formatName } from 'utils/util';

export const HelpView = () => {
    const { t } = useTranslation();
    const { pushApiError, removeApiError, isApiErrorSet } = useApiErrorHandling({});
    const { data: admins } = useErrorHandledQuery(
        ['admins'],
        () => getApis().userApi.getSystemAdmins(),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    return (
        <PageContainer
            header={
                <Grid item xs={12}>
                    <PageHeaderText text={t('help.header')} />
                </Grid>
            }
        >
            <Container>
                {admins?.map(({ email, name, surname }) => (
                    <Box key={email} sx={{ ...stylesRow, mb: 2 }}>
                        <Typography variant="h6" sx={{ mr: 4 }}>
                            {formatName(name, surname)}
                        </Typography>
                        <Typography variant="h6">{email}</Typography>
                    </Box>
                ))}
            </Container>
        </PageContainer>
    );
};
