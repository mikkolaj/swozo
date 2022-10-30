/* eslint-disable react/jsx-key */
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import EditIcon from '@mui/icons-material/Edit';
import { Box, Divider, Stack, TextField, Typography } from '@mui/material';
import { CourseDetailsDto } from 'api';
import { ApiError, ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { stylesRowCenteredVertical } from 'common/styles';
import { useMeQuery } from 'hooks/query/useMeQuery';
import { buildErrorHandler, HandlerConfig, useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { updateCourseCache } from 'pages/Course/utils';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-toastify';
import { triggerError } from 'services/features/error/errorSlice';
import { useAppDispatch } from 'services/store';
import { isSame } from 'utils/roles';
import { formatDate, formatName } from 'utils/util';
import { TeacherSection } from './TeacherSection';

type Props = {
    course: CourseDetailsDto;
};

export const ParticipantsListView = ({ course }: Props) => {
    const { t } = useTranslation();
    const [editMode, setEditMode] = useState(false);
    const dispatch = useAppDispatch();
    const { me } = useMeQuery();
    const [newStudentEmail, setNewStudentEmail] = useState('');
    const queryClient = useQueryClient();
    const [errorHandlers] = useState<HandlerConfig>({
        [ErrorType.USER_NOT_FOUND]: buildErrorHandler(() => {
            dispatch(
                triggerError({ message: t('course.options.participants.addStudent.error.USER_NOT_FOUND') })
            );
        }, false),
        [ErrorType.ALREADY_A_MEMBER]: buildErrorHandler(() => {
            dispatch(
                triggerError({ message: t('course.options.participants.addStudent.error.ALREADY_A_MEMBER') })
            );
        }, false),
    });
    const { isApiError, pushApiError, consumeErrorAction, errorHandler } = useApiErrorHandling(errorHandlers);
    const addStudentMutation = useMutation(
        (email: string) =>
            getApis().courseApi.addStudentToCourse({
                courseId: course.id,
                modifyParticipantRequest: { email },
            }),
        {
            onSuccess: (mutatedCourse, email) => {
                toast.success(t('course.options.participants.addStudent.success', { email }));
                updateCourseCache(queryClient, mutatedCourse);
            },
            onError: (error: ApiError) => {
                pushApiError(error);
            },
        }
    );

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    return (
        <Stack>
            <Typography variant={'h5'} gutterBottom>
                {t('course.options.participants.teacher')}
            </Typography>
            <TeacherSection course={course} />

            <Divider />

            <Box sx={{ ...stylesRowCenteredVertical, justifyContent: 'space-between' }}>
                <Typography variant={'h5'} gutterBottom sx={{ mt: 2 }}>
                    {t('course.options.participants.students.label')}
                </Typography>
                {isSame(me, course.teacher) && (
                    <ButtonWithIconAndText
                        onClick={() => setEditMode((editMode) => !editMode)}
                        textI18n="course.options.participants.editMode"
                        Icon={EditIcon}
                    />
                )}
            </Box>
            {editMode && (
                <Box sx={{ ...stylesRowCenteredVertical, ml: 2, mb: 4 }}>
                    <TextField
                        variant="standard"
                        type="email"
                        label={t('course.options.participants.addStudent.label')}
                        value={newStudentEmail}
                        onChange={(v) => setNewStudentEmail(v.target.value)}
                    />
                    <ButtonWithIconAndText
                        sx={{ mt: 1.5 }}
                        onClick={() => addStudentMutation.mutateAsync(newStudentEmail)}
                        textI18n="course.options.participants.addStudent.button"
                        Icon={AddCircleOutlineIcon}
                    />
                </Box>
            )}

            <StackedList
                header={
                    <StackedListHeader
                        proportions={[3, 3, 2]}
                        items={['name', 'email', 'joinDate'].map((label) => (
                            <Typography variant="body1" color="GrayText">
                                {t(`course.options.participants.students.table.${label}`)}
                            </Typography>
                        ))}
                    />
                }
                content={
                    <StackedListContent
                        proportions={[3, 3, 2]}
                        items={course.students}
                        itemKeyExtractor={({ participant }) => participant.email}
                        itemRenderer={({ participant, joinedAt }) => [
                            <Typography variant="body1">
                                {formatName(participant.name, participant.surname)}
                            </Typography>,
                            <Typography variant="body1">{participant.email}</Typography>,
                            <Typography variant="body1">{formatDate(joinedAt)}</Typography>,
                        ]}
                    />
                }
            />
        </Stack>
    );
};
