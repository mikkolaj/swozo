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

export const AttendedCoursesList = ({ user }: Props) => {
    const { t } = useTranslation();

    return user.roles.includes(UserAdminDetailsDtoRolesEnum.Student) ? (
        <>
            <Box sx={{ ...stylesRow, justifyContent: 'center' }}>
                <Typography sx={{ ml: 2, mb: 2 }} variant="h5">
                    {t('admin.userDetails.attendedCourses.label')}
                </Typography>
            </Box>
            <StackedList
                /* eslint-disable react/jsx-key */
                header={
                    <StackedListHeader
                        proportions={[4, 3, 2, 3]}
                        items={['name', 'teacher', 'createdAt'].map((label) => (
                            <Typography variant="body1" color="GrayText">
                                {t(`admin.userDetails.attendedCourses.${label}`)}
                            </Typography>
                        ))}
                    />
                }
                content={
                    <StackedListContent
                        proportions={[4, 3, 2, 3]}
                        items={user.attendedCourses ?? []}
                        itemKeyExtractor={({ joinUUID }) => joinUUID}
                        itemRenderer={({ name, teacher, creationTime, joinUUID }) => [
                            <LinkedTypography
                                variant="body1"
                                to={PageRoutes.JoinCourse(joinUUID)}
                                text={name}
                                decorated
                            />,
                            <NoOverflowTypography variant="body1">{teacher.email}</NoOverflowTypography>,
                            <NoOverflowTypography variant="body1">
                                {formatDate(creationTime)}
                            </NoOverflowTypography>,
                        ]}
                        emptyItemsComponent={
                            <Box sx={{ ...stylesColumn, pt: 1, alignItems: 'center' }}>
                                <Typography variant="h6">
                                    {t('admin.userDetails.attendedCourses.empty')}
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
