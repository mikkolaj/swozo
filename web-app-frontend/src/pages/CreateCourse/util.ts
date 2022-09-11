import { CreateActivityRequest, CreateCourseRequest, ServiceModuleDetailsDto } from 'api';
import { Dayjs } from 'dayjs';

export type ActivityValues = {
    name: string;
    description: string;
    lessonModules: ServiceModuleDetailsDto[];
    generalModules: ServiceModuleDetailsDto[];
    instructions: string;
    startTime: Dayjs;
    endTime: Dayjs;
};

export type CourseValues = {
    name: string;
    subject: string;
    description: string;
    numberOfActivities: number;
    numberOfStudents: number;
    students: string[];
};

const buildCreateActivityRequest = (activity: ActivityValues): CreateActivityRequest => {
    return {
        name: activity.name,
        description: activity.description,
        startTime: activity.startTime.toDate(),
        endTime: activity.endTime.toDate(),
        instructionsFromTeacher: [
            {
                body: activity.instructions, //TODO
            },
        ],
        selectedModulesIds: [...activity.lessonModules /*, ...activity.generalModules*/].map(({ id }) => id), // TODO
    };
};

/**
 *  Assumes validated data
 */
export const buildCreateCourseRequest = (
    course: CourseValues,
    activities: ActivityValues[]
): CreateCourseRequest => {
    return {
        name: course.name,
        description: course.description,
        subject: course.subject,
        expectedStudentCount: course.numberOfStudents,
        studentEmails: course.students.filter((email) => email !== ''), // TODO
        activities: activities.map(buildCreateActivityRequest),
    };
};
