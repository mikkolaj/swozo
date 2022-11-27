import { Box, Divider, Typography } from '@mui/material';
import { UserAdminDetailsDto, UserAdminDetailsDtoRolesEnum } from 'api';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import { NoOverflowTypography } from 'common/Styled/NoOverflowTypography';
import { stylesColumn, stylesRow } from 'common/styles';
import { useTranslation } from 'react-i18next';
import { PageRoutes } from 'utils/routes';
import { formatDate } from 'utils/util';

type Props = {
    user: UserAdminDetailsDto;
};

export const CreatedServiceModulesList = ({ user }: Props) => {
    const { t } = useTranslation();

    return user.roles.includes(UserAdminDetailsDtoRolesEnum.TechnicalTeacher) ? (
        <>
            <Box sx={{ ...stylesRow, justifyContent: 'center' }}>
                <Typography sx={{ ml: 2, mb: 2 }} variant="h5">
                    {t('admin.userDetails.createdModules.label')}
                </Typography>
            </Box>
            <StackedList
                /* eslint-disable react/jsx-key */
                header={
                    <StackedListHeader
                        proportions={[4, 3, 2, 3]}
                        items={['name', 'service', 'createdAt', 'usedBy'].map((label) => (
                            <Typography variant="body1" color="GrayText">
                                {t(`admin.userDetails.createdModules.${label}`)}
                            </Typography>
                        ))}
                    />
                }
                content={
                    <StackedListContent
                        proportions={[4, 3, 2, 3]}
                        items={user.createdModules ?? []}
                        itemKeyExtractor={({ id }) => id}
                        itemRenderer={({ id, name, createdAt, serviceName, usedInActivitiesCount }) => [
                            <LinkedTypography
                                variant="body1"
                                to={PageRoutes.PublicModule(id)}
                                text={name}
                                decorated
                            />,
                            <NoOverflowTypography variant="body1">{serviceName}</NoOverflowTypography>,
                            <NoOverflowTypography variant="body1">
                                {formatDate(createdAt)}
                            </NoOverflowTypography>,
                            <NoOverflowTypography sx={{ margin: 'auto' }} variant="body1">
                                {usedInActivitiesCount}
                            </NoOverflowTypography>,
                        ]}
                        emptyItemsComponent={
                            <Box sx={{ ...stylesColumn, pt: 1, alignItems: 'center' }}>
                                <Typography variant="h6">
                                    {t('admin.userDetails.createdModules.empty')}
                                </Typography>
                            </Box>
                        }
                    />
                }
                /* eslint-enable react/jsx-key */
            />
            <Divider sx={{ mt: 3 }} />
        </>
    ) : (
        <></>
    );
};
