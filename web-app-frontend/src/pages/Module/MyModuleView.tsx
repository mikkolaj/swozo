import { Box, Grid, Typography } from '@mui/material';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { ReadonlyField } from 'common/Input/Readonly/ReadonlyField';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { InstructionView } from 'common/Styled/InstructionView';
import { stylesColumnCenteredVertical, stylesRowCenteredHorizontal } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useRequiredParams } from 'hooks/useRequiredParams';
import { DynamicReadonlyField } from 'pages/CreateModule/components/dynamic/DynamicReadonlyField';
import { ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';

export const MyModuleView = () => {
    const [moduleId] = useRequiredParams(['moduleId']);
    const { t } = useTranslation();

    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: serviceModule } = useErrorHandledQuery(
        ['modules', moduleId],
        () => getApis().serviceModuleApi.getServiceModule({ id: +moduleId }),
        pushApiError,
        removeApiError
    );

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    if (!serviceModule) {
        return <PageContainerWithLoader />;
    }

    console.log(serviceModule);

    return (
        <PageContainer
            sx={{ p: 0 }}
            header={
                <>
                    <Grid item xs={6}>
                        <Typography variant="h4" component="div">
                            {t('myModule.header', { name: serviceModule.name })}
                        </Typography>
                    </Grid>
                </>
            }
        >
            <Grid container sx={{ m: 2, mt: 0 }}>
                <Grid item xs={12}>
                    <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mb: 4 }}>
                        <Typography variant="h3">{serviceModule.name}</Typography>
                    </Box>
                </Grid>
                <Grid item xs={12}>
                    <Typography variant="h5">{t('myModule.generalInfo')}</Typography>
                    <Box sx={{ ...stylesColumnCenteredVertical }}>
                        <StyledReadonlyField
                            wrapperSx={{ width: '50%' }}
                            value={serviceModule.description}
                            textFieldProps={{ multiline: true, fullWidth: true }}
                            i18nLabel="myModule.description"
                        />
                        <StyledReadonlyField value={serviceModule.subject} i18nLabel="myModule.subject" />
                        <StyledReadonlyField
                            value={serviceModule.serviceName}
                            i18nLabel="myModule.serviceName"
                        />
                        {Object.entries(serviceModule.dynamicFields).map(([fieldName, field]) => (
                            <Box key={fieldName} sx={{ m: 1 }}>
                                <DynamicReadonlyField
                                    field={field}
                                    onInteractionError={(err) => pushApiError(err as ApiError)}
                                />
                            </Box>
                        ))}
                        <Box sx={{ m: 1 }}>
                            <Typography>{t('myModule.teacherInstruction')}</Typography>
                            <InstructionView
                                wrapperSx={{ maxWidth: '50%' }}
                                instruction={serviceModule.teacherInstruction}
                            />
                        </Box>
                        <Box sx={{ m: 1 }}>
                            <Typography>{t('myModule.studentInstruction')}</Typography>
                            <InstructionView
                                wrapperSx={{ maxWidth: '50%' }}
                                instruction={serviceModule.studentInstruction}
                            />
                        </Box>
                    </Box>
                </Grid>
            </Grid>
        </PageContainer>
    );
};

const StyledReadonlyField = ({ wrapperSx, ...props }: ComponentProps<typeof ReadonlyField>) => (
    <ReadonlyField wrapperSx={{ m: 1, ...wrapperSx }} {...props} />
);
