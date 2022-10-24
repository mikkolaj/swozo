import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { Box, Grid, IconButton, Typography } from '@mui/material';
import { blue } from '@mui/material/colors';
import { CourseDetailsDto } from 'api';
import { PasswordLikeText } from 'common/Styled/PasswordLikeText';
import { stylesColumn, stylesRowCenteredVertical } from 'common/styles';
import { useMeQuery } from 'hooks/query/useMeQuery';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';
import { isSame } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { formatName } from 'utils/util';

type Props = {
    course: CourseDetailsDto;
};

const HIDDEN_PASSWORD_PLACEHOLDER = '*****';

export const TeacherSection = ({ course }: Props) => {
    const { t } = useTranslation();
    const { me } = useMeQuery();
    const joinCourseUrl = PageRoutes.withOrigin(PageRoutes.JoinCourse(course.joinUUID));

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
            <Box sx={{ ...stylesColumn, mt: 2 }}>
                <Box sx={stylesRowCenteredVertical}>
                    <Typography>
                        {t('course.options.participants.joinUrl')}
                        <a style={{ textDecoration: 'none', color: blue[800] }} href={joinCourseUrl}>
                            {joinCourseUrl}
                        </a>
                    </Typography>
                    <IconButton
                        color="primary"
                        onClick={() => {
                            navigator.clipboard.writeText(joinCourseUrl);
                            toast.success(t('course.options.participants.copied'));
                        }}
                    >
                        <ContentCopyIcon sx={{ transform: 'scale(0.9)', ml: 0.5, mt: -0.3 }} />
                    </IconButton>
                </Box>
                {course.coursePassword && isSame(me, course.teacher) && (
                    <PasswordLikeText
                        textSupplier={(isVisible) =>
                            t('course.options.participants.coursePassword', {
                                password: isVisible ? course.coursePassword : HIDDEN_PASSWORD_PLACEHOLDER,
                            })
                        }
                    />
                )}
            </Box>
        </Box>
    );
};
