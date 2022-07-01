import { ActivityDetailsReq, CourseDetailsReq, ServiceModuleDetailsResp } from 'api';
import { Dayjs } from 'dayjs';

export type ActivityValues = {
    name: string;
    description: string;
    lessonModules: ServiceModuleDetailsResp[];
    generalModules: ServiceModuleDetailsResp[];
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

const buildCreateActivityRequest = (activity: ActivityValues): ActivityDetailsReq => {
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
): CourseDetailsReq => {
    return {
        name: course.name,
        description: course.description,
        subject: course.subject,
        expectedStudentCount: course.numberOfStudents,
        studentEmails: course.students.filter((email) => email !== ''), // TODO
        activityDetailReqs: activities.map(buildCreateActivityRequest),
    };
};
