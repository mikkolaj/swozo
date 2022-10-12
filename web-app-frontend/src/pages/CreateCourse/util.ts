import { CreateActivityRequest, CreateCourseRequest, ServiceModuleDetailsDto } from 'api';
import { ApiError } from 'api/errors';
import { SlideValues2 } from 'common/SlideForm/util';
import dayjs, { Dayjs } from 'dayjs';
import { FormikErrors } from 'formik';
import _ from 'lodash';
import { TFunction } from 'react-i18next';
import { formatDateTime, prepareErrorForDisplay, prepareFormikValidationErrors, withDate } from 'utils/util';

export const DEFAULT_ACTIVITY_LENGTH_MINUTES = 90;
export const DEFAULT_MIN_TIME_OFFSET = 30;

export type FormValues = SlideValues2<CourseValues, ActivitesFormValues>;

const COURSE_SLIDE_DATA_NAME = 'course';
const FIELD_SEPARATOR = '.';

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
    expectedStudentCount: number;
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
    expectedStudentCount: 2,
    password: undefined,
});

export const initialActivityValues = (): ActivityValues => ({
    name: 'x',
    description: '',
    lessonModules: [],
    generalModules: [],
    instructions: '',
    date: dayjs(),
    startTime: dayjs().add(DEFAULT_MIN_TIME_OFFSET, 'minutes'),
    endTime: dayjs().add(DEFAULT_ACTIVITY_LENGTH_MINUTES + DEFAULT_MIN_TIME_OFFSET, 'minutes'),
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

const argFormatter = (argName: string, argVal: string) => {
    switch (argName) {
        case 'minStartTime':
            return formatDateTime(new Date(argVal));
        default:
            return argVal;
    }
};

export const formatErrors = (t: TFunction, error: ApiError): FormikErrors<FormValues> => {
    const coursePrefix = COURSE_SLIDE_DATA_NAME + FIELD_SEPARATOR;

    return prepareFormikValidationErrors(
        error,
        (key) =>
            key.startsWith(coursePrefix)
                ? key.replace(COURSE_SLIDE_DATA_NAME, COURSE_SLIDE)
                : `${ACTIVITIES_SLIDE}${FIELD_SEPARATOR}${key}`,
        (error) => prepareErrorForDisplay(t, 'createCourse', error, argFormatter)
    );
};

export const buildCreateCourseRequest = (
    course: CourseValues,
    activities: ActivityValues[]
): CreateCourseRequest => {
    return {
        name: course.name,
        description: course.description,
        subject: course.subject,
        expectedStudentCount: course.expectedStudentCount,
        activities: activities.map(buildCreateActivityRequest),
        password: course.password,
    };
};
