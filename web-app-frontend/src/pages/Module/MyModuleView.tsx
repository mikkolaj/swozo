import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import { Box, Grid, Typography } from '@mui/material';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { DynamicReadonlyField } from 'common/DynamicFields/Output/DynamicReadonlyField';
import { ReadonlyField } from 'common/Input/Readonly/ReadonlyField';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { InstructionView } from 'common/Styled/InstructionView';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import { stylesColumnCenteredVertical, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useDeleteServiceModule } from 'hooks/query/useDeleteServiceModule';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useRequiredParams } from 'hooks/useRequiredParams';
import _ from 'lodash';
import { ComponentProps, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { formatDate } from 'utils/util';
import { SandboxModal } from './components/SandboxModal';

export const MyModuleView = () => {
    const [moduleId] = useRequiredParams(['moduleId']);
    const [sandboxModalOpen, setSandboxModalOpen] = useState(true);
    const navigate = useNavigate();
    const { t } = useTranslation();

    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: serviceModule } = useErrorHandledQuery(
        ['modules', moduleId, 'details'],
        () => getApis().serviceModuleApi.getServiceModule({ serviceModuleId: +moduleId }),
        pushApiError,
        removeApiError
    );

    const { data: usage } = useErrorHandledQuery(
        ['modules', moduleId, 'usage'],
        () => getApis().serviceModuleApi.getUsage({ serviceModuleId: +moduleId, limit: 100, offset: 0 }),
        pushApiError,
        removeApiError
    );

    const { serviceModuleDeleteMutation } = useDeleteServiceModule(pushApiError);

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    if (!serviceModule) {
        return <PageContainerWithLoader />;
    }

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
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <ButtonWithIconAndText
                            textI18n="myModule.sandbox"
                            Icon={SportsEsportsIcon}
                            onClick={() => setSandboxModalOpen(true)}
                        />
                        <ButtonWithIconAndText
                            textI18n="myModule.edit"
                            Icon={EditIcon}
                            onClick={() => navigate(PageRoutes.EditModule(serviceModule.id))}
                        />
                        {serviceModule.usedInActivitiesCount === 0 && (
                            <ButtonWithIconAndText
                                textI18n="myModule.delete"
                                Icon={DeleteIcon}
                                onClick={() => serviceModuleDeleteMutation.mutate(`${serviceModule.id}`)}
                            />
                        )}
                    </Grid>
                </>
            }
        >
            <Grid container sx={{ p: 2, pt: 0 }}>
                <Grid item xs={12}>
                    <Typography variant="h5" gutterBottom>
                        {t('myModule.generalInfo')}
                    </Typography>
                    <Box sx={{ ...stylesColumnCenteredVertical }}>
                        <StyledReadonlyField
                            wrapperSx={{ width: '50%' }}
                            value={serviceModule.description}
                            textFieldProps={{ multiline: true, fullWidth: true }}
                            i18nLabel="myModule.description"
                        />
                        <StyledReadonlyField
                            value={_.capitalize(serviceModule.subject)}
                            i18nLabel="myModule.subject"
                        />
                        <StyledReadonlyField
                            value={_.capitalize(serviceModule.serviceName)}
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
                <Grid item xs={12} sx={{ mt: 4 }}>
                    <Typography variant="h5" gutterBottom sx={{ mb: 2 }}>
                        {t('myModule.usedBy.label')}
                    </Typography>
                    {usage && usage.length > 0 ? (
                        <StackedList
                            /* eslint-disable react/jsx-key */
                            header={
                                <StackedListHeader
                                    proportions={[3, 3, 3, 3]}
                                    items={['courseName', 'activityName', 'teacherEmail', 'addedAt'].map(
                                        (label) => (
                                            <Typography variant="body1" color="GrayText">
                                                {t(`myModule.usedBy.info.${label}`)}
                                            </Typography>
                                        )
                                    )}
                                />
                            }
                            content={
                                <StackedListContent
                                    proportions={[3, 3, 3, 3]}
                                    items={usage ?? []}
                                    itemKeyExtractor={({ courseId, activityName }) =>
                                        `${courseId}_${activityName}`
                                    }
                                    itemRenderer={({
                                        courseName,
                                        activityName,
                                        courseCreator,
                                        addedAt,
                                        courseId,
                                    }) => [
                                        <LinkedTypography
                                            variant="body1"
                                            to={PageRoutes.Course(courseId)}
                                            text={courseName}
                                            decorated
                                        />,
                                        <Typography variant="body1">{activityName}</Typography>,
                                        <Typography variant="body1">{courseCreator.email}</Typography>,
                                        <Typography variant="body1">{formatDate(addedAt)}</Typography>,
                                    ]}
                                />
                            }
                            /* eslint-enable react/jsx-key */
                        />
                    ) : (
                        <Typography>{t('myModule.usedBy.unused')}</Typography>
                    )}
                </Grid>
            </Grid>
            <SandboxModal
                open={sandboxModalOpen}
                onClose={() => setSandboxModalOpen(false)}
                serviceModule={serviceModule}
            />
        </PageContainer>
    );
};

const StyledReadonlyField = ({ wrapperSx, ...props }: ComponentProps<typeof ReadonlyField>) => (
    <ReadonlyField wrapperSx={{ m: 1, ...wrapperSx }} {...props} />
);
