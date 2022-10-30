import { CourseSummaryDto } from 'api';
import { formatName, naiveTextCompare } from 'utils/util';

const matchesTextSearch = (course: CourseSummaryDto, searchPhrase: string) => {
    return (
        naiveTextCompare(course.name, searchPhrase) ||
        naiveTextCompare(formatName(course.teacher.name, course.teacher.surname), searchPhrase) ||
        naiveTextCompare(course.teacher.email, searchPhrase)
    );
};

export const filterCourses = (courses: CourseSummaryDto[], searchPhrase: string) => {
    return courses.filter((course) => matchesTextSearch(course, searchPhrase));
};
