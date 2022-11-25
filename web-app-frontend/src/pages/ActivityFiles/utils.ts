import {
    ActivityDetailsDto,
    ActivityFilesDto,
    ActivityModuleDetailsDto,
    FileDto,
    TeacherActivityFilesDto,
    UserDetailsDto,
} from 'api';
import _ from 'lodash';

export type UserActivityFile = {
    file: FileDto;
    activityModule: ActivityModuleDetailsDto;
};

export type StudentActivityFile = {
    file: FileDto;
    student: UserDetailsDto;
};

export type StudentAcitivtyFilesGroupedByModule = [ActivityModuleDetailsDto, StudentActivityFile[]][];

const getActivityModuleById = (activity: ActivityDetailsDto) =>
    Object.fromEntries(activity.activityModules.map((module) => [module.id, module]));

export const toUserActivityFiles = (
    activity: ActivityDetailsDto,
    filesDto: ActivityFilesDto
): UserActivityFile[] => {
    const activityModuleById = getActivityModuleById(activity);

    return Object.entries(filesDto.activityModuleIdToUserFiles).flatMap(([activityModuleId, files]) =>
        files.map((file) => ({ file, activityModule: activityModuleById[activityModuleId] }))
    );
};

export const toUserActivityFilesGroupedByModuleId = (
    teacherActivityFiles: TeacherActivityFilesDto,
    activity: ActivityDetailsDto
): StudentAcitivtyFilesGroupedByModule => {
    const activityModuleById = getActivityModuleById(activity);

    const activityFilesByModuleId = _.chain(Object.entries(teacherActivityFiles.userIdToUserFiles))
        .flatMap(([userId, activityFiles]) =>
            Object.entries(activityFiles.activityModuleIdToUserFiles).flatMap(([activityModuleId, files]) =>
                files.map((file) => ({ userId, activityModuleId: +activityModuleId, file }))
            )
        )
        .groupBy(({ activityModuleId }) => activityModuleId)
        .mapValues((values) =>
            values.map(({ file, userId }) => ({
                file,
                student: teacherActivityFiles.userIdToUserDetails[userId],
            }))
        )
        .value();

    return Object.entries(activityFilesByModuleId).map(([k, v]) => [activityModuleById[k], v]);
};
