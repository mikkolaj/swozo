import { Box } from '@mui/system';
import { ActivityValues, CourseValues } from 'pages/CreateCourse/util';

type Props = {
    course: CourseValues;
    activities: ActivityValues[];
};

export const Summary = ({ course, activities }: Props) => {
    // TODO: display this somehow
    return (
        <Box>
            {' '}
            Tu kiedyś będzie podsumowanie dla kursu {course.name} z aktywnościami{' '}
            {activities.map((activity) => (
                <Box key={activity.name}>-{activity.name}</Box>
            ))}{' '}
        </Box>
    );
};
