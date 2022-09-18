import { Box, Divider, Grid, Stack, Typography } from '@mui/material';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { PasswordLikeText } from 'common/Styled/PasswordLikeText';
import { CourseContext } from 'pages/Course/CourseView';
import { useContext } from 'react';
import { useTranslation } from 'react-i18next';
import { TEACHER, WithRole } from 'utils/roles';
import { formatName } from 'utils/util';
import { ParticipantInfo } from './ParticipantInfo';

const HIDDEN_PASSWORD_PLACEHOLDER = '*****';

export const ParticipantsListView = () => {
    const { t } = useTranslation();
    const course = useContext(CourseContext);

    if (!course) {
        return <PageContainerWithLoader />;
    }

    return (
        <Stack>
            <Typography variant={'h5'} gutterBottom>
                {t('course.options.participants.teacher')}
            </Typography>
            <Box sx={{ ml: 3 }}>
                <Grid container>
                    <Grid item>
                        <Typography variant="h6">
                            {formatName(course.teacher.name, course.teacher.surname)}
                        </Typography>
                    </Grid>
                    <Grid item sx={{ ml: 4 }}>
                        <Typography variant="h6">{course.teacher.email}</Typography>
                    </Grid>
                </Grid>
                <WithRole roles={[TEACHER]}>
                    <PasswordLikeText
                        textSupplier={(isVisible) =>
                            t('course.options.participants.coursePassword', {
                                // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                                password: isVisible ? course.coursePassword! : HIDDEN_PASSWORD_PLACEHOLDER,
                            })
                        }
                    />
                </WithRole>
            </Box>
            <Divider />
            <Typography variant={'h5'} gutterBottom sx={{ mt: 2 }}>
                {t('course.options.participants.students')}
            </Typography>
            <Stack sx={{ ml: 3 }}>
                {course.students.map((student) => (
                    <ParticipantInfo
                        key={student.participant.email}
                        participant={student.participant}
                        joinedAt={student.joinedAt}
                    />
                ))}
            </Stack>

            {/* <Box sx={{ p: 1, boxShadow: 2, ml: 3 }}> */}
        </Stack>
    );
};
