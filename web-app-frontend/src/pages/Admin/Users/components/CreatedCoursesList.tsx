import { Box, Divider, Typography } from '@mui/material';
import { UserAdminDetailsDto, UserAdminDetailsDtoRolesEnum } from 'api';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import { NoOverflowTypography } from 'common/Styled/NoOverflowTypography';
import { stylesColumn, stylesRow } from 'common/styles';
import _ from 'lodash';
import { useTranslation } from 'react-i18next';
import { PageRoutes } from 'utils/routes';
import { formatDate } from 'utils/util';

type Props = {
    user: UserAdminDetailsDto;
};

export const CreatedCoursesList = ({ user }: Props) => {
    const { t } = useTranslation();

    return user.roles.includes(UserAdminDetailsDtoRolesEnum.Teacher) ? (
        <>
            <Box sx={{ ...stylesRow, justifyContent: 'center' }}>
                <Typography sx={{ ml: 2, mb: 2 }} variant="h5">
                    {t('admin.userDetails.createdCourses.label')}
                </Typography>
            </Box>
            <StackedList
                /* eslint-disable react/jsx-key */
                header={
                    <StackedListHeader
                        proportions={[4, 3, 2, 3]}
                        items={['name', 'subject', 'createdAt'].map((label) => (
                            <Typography variant="body1" color="GrayText">
                                {t(`admin.userDetails.createdCourses.${label}`)}
                            </Typography>
                        ))}
                    />
                }
                content={
                    <StackedListContent
                        proportions={[4, 3, 2, 3]}
                        items={user.createdCourses ?? []}
                        itemKeyExtractor={({ joinUUID }) => joinUUID}
                        itemRenderer={({ name, subject, creationTime, joinUUID }) => [
                            <LinkedTypography
                                variant="body1"
                                to={PageRoutes.JoinCourse(joinUUID)}
                                text={name}
                                decorated
                            />,
                            <NoOverflowTypography variant="body1">
                                {_.capitalize(subject)}
                            </NoOverflowTypography>,
                            <NoOverflowTypography variant="body1">
                                {formatDate(creationTime)}
                            </NoOverflowTypography>,
                        ]}
                        emptyItemsComponent={
                            <Box sx={{ ...stylesColumn, pt: 1, alignItems: 'center' }}>
                                <Typography variant="h6">
                                    {t('admin.userDetails.createdCourses.empty')}
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
