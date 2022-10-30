/* eslint-disable react/jsx-key */
import { Box, Chip, Divider, Typography } from '@mui/material';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { stylesRow, stylesRowCenteredHorizontal } from 'common/styles';
import _ from 'lodash';
import { ActivityValues, CourseValues } from 'pages/CreateCourse/util';
import { useTranslation } from 'react-i18next';
import { formatDate, formatTime, withDate } from 'utils/util';

type Props = {
    course: CourseValues;
    activities: ActivityValues[];
};

export const Summary = ({ course, activities }: Props) => {
    const { t } = useTranslation();

    return (
        <Box>
            <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mb: 4 }}>
                <Typography sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }} variant="h3">
                    {course.name}
                </Typography>
            </Box>
            <StackedList
                header={
                    <StackedListHeader
                        proportions={[5, 4, 3]}
                        items={[
                            <Typography variant="body1" color="GrayText">
                                {t('createCourse.slides.2.form.activityName')}
                            </Typography>,
                            <Typography variant="body1" color="GrayText">
                                {t('createCourse.slides.2.form.modules')}
                            </Typography>,
                            <Typography variant="body1" color="GrayText">
                                {t('createCourse.slides.2.form.at')}
                            </Typography>,
                        ]}
                    />
                }
                content={
                    <StackedListContent
                        proportions={[5, 4, 3]}
                        items={_.sortBy(activities, ({ startTime, date }) =>
                            withDate(startTime, date).toDate().getTime()
                        )}
                        itemKeyExtractor={(activity) => activity.name}
                        itemRenderer={(item) => [
                            <Typography
                                variant="body1"
                                sx={{ overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}
                            >
                                {item.name}
                            </Typography>,
                            <Box sx={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center' }}>
                                {[...item.generalModules, ...item.lessonModules].map((mod) => (
                                    <Chip sx={{ margin: 0.5 }} key={mod.id} label={mod.name} />
                                ))}
                            </Box>,
                            <Box sx={{ margin: 'auto', ...stylesRow }}>
                                <Typography>{formatDate(item.date.toDate())}</Typography>
                                <Typography sx={{ ml: 1.5 }} variant="body1">{`${formatTime(
                                    item.startTime.toDate()
                                )} - ${formatTime(item.endTime.toDate())}`}</Typography>
                            </Box>,
                        ]}
                    />
                }
            />
            <Divider sx={{ my: 4 }} />

            <Box sx={{ mb: 1 }}>
                <Typography variant="h5">{t('createCourse.slides.2.disclaimer.title')}</Typography>
            </Box>
            <Box>
                {[...t('createCourse.slides.2.disclaimer.lines', { returnObjects: true })].map(
                    (line, idx) => (
                        <Typography key={idx} variant="subtitle1">
                            {line}
                        </Typography>
                    )
                )}
            </Box>
        </Box>
    );
};
