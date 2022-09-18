import { Box, Grid, Typography } from '@mui/material';
import { CourseDetailsDto } from 'api';
import { PasswordLikeText } from 'common/Styled/PasswordLikeText';
import { t } from 'i18next';
import { TEACHER, WithRole } from 'utils/roles';
import { formatName } from 'utils/util';

type Props = {
    course: CourseDetailsDto;
};

const HIDDEN_PASSWORD_PLACEHOLDER = '*****';

export const TeacherSection = ({ course }: Props) => {
    return (
        <Box sx={{ ml: 3, mb: 2 }}>
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
            {course.coursePassword && (
                <WithRole roles={[TEACHER]}>
                    <PasswordLikeText
                        textSupplier={(isVisible) =>
                            t('course.options.participants.coursePassword', {
                                password: isVisible ? course.coursePassword : HIDDEN_PASSWORD_PLACEHOLDER,
                            })
                        }
                    />
                </WithRole>
            )}
        </Box>
    );
};
