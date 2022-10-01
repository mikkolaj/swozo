import { CreateActivityRequest, CreateCourseRequest, ServiceModuleDetailsDto } from 'api';
import { SlideValues2 } from 'common/SlideForm/util';
import dayjs, { Dayjs } from 'dayjs';
import _ from 'lodash';
import { withDate } from 'utils/util';

export const DEFAULT_ACTIVITY_LENGTH_MINUTES = 90;

export type FormValues = SlideValues2<CourseValues, ActivitesFormValues>;

export const COURSE_SLIDE = '0';
export const ACTIVITIES_SLIDE = '1';

export type ActivityValues = {
    name: string;
    description: string;
    lessonModules: ServiceModuleDetailsDto[];
    generalModules: ServiceModuleDetailsDto[];
    instructions: string;
    date: Dayjs;
    startTime: Dayjs;
    endTime: Dayjs;
};

export type CourseValues = {
    name: string;
    subject: string;
    description: string;
    numberOfActivities: number;
    numberOfStudents: number;
    password?: string;
};

export type ActivitesFormValues = {
    activities: ActivityValues[];
};

export const initialCourseValues = (): CourseValues => ({
    name: 'Wprowadzenie do Pythona',
    subject: 'Informatyka',
    description: '',
    numberOfActivities: 1,
    numberOfStudents: 2,
    password: undefined,
});

export const initialActivityValues = (): ActivityValues => ({
    name: '',
    description: '',
    lessonModules: [],
    generalModules: [],
    instructions: '',
    date: dayjs(),
    startTime: dayjs(),
    endTime: dayjs().add(DEFAULT_ACTIVITY_LENGTH_MINUTES, 'minutes'),
});

export const resizeActivityValuesList = (
    currentValues: ActivityValues[],
    targetSize: number
): ActivityValues[] => {
    const currentSize = currentValues.length;

    if (targetSize > currentSize) {
        return [...currentValues, ..._.range(targetSize - currentSize).map((_) => initialActivityValues())];
    }

    return currentValues.slice(0, targetSize);
};

const buildCreateActivityRequest = (activity: ActivityValues): CreateActivityRequest => {
    return {
        name: activity.name,
        description: activity.description,
        startTime: withDate(activity.startTime, activity.date).toDate(),
        endTime: withDate(activity.endTime, activity.date).toDate(),
        instructionsFromTeacher: [
            {
                untrustedPossiblyDangerousHtml: activity.instructions,
            },
        ],
        selectedModulesIds: [...activity.lessonModules /*, ...activity.generalModules*/].map(({ id }) => id), // TODO
    };
};

export const buildCreateCourseRequest = (
    course: CourseValues,
    activities: ActivityValues[]
): CreateCourseRequest => {
    return {
        name: course.name,
        description: course.description,
        subject: course.subject,
        expectedStudentCount: course.numberOfStudents,
        activities: activities.map(buildCreateActivityRequest),
        password: course.password,
    };
};
