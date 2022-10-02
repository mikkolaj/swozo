/* tslint:disable */
/* eslint-disable */
/**
 * OpenAPI definition
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
import {
    Course,
    CourseFromJSON,
    CourseFromJSONTyped,
    CourseToJSON,
} from './Course';
import {
    User,
    UserFromJSON,
    UserFromJSONTyped,
    UserToJSON,
} from './User';
import {
    UserCourseId,
    UserCourseIdFromJSON,
    UserCourseIdFromJSONTyped,
    UserCourseIdToJSON,
} from './UserCourseId';

/**
 * 
 * @export
 * @interface UserCourseData
 */
export interface UserCourseData {
    /**
     * 
     * @type {UserCourseId}
     * @memberof UserCourseData
     */
    id?: UserCourseId;
    /**
     * 
     * @type {User}
     * @memberof UserCourseData
     */
    user?: User;
    /**
     * 
     * @type {Course}
     * @memberof UserCourseData
     */
    course?: Course;
    /**
     * 
     * @type {Date}
     * @memberof UserCourseData
     */
    joinedAt?: Date;
}

export function UserCourseDataFromJSON(json: any): UserCourseData {
    return UserCourseDataFromJSONTyped(json, false);
}

export function UserCourseDataFromJSONTyped(json: any, ignoreDiscriminator: boolean): UserCourseData {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : UserCourseIdFromJSON(json['id']),
        'user': !exists(json, 'user') ? undefined : UserFromJSON(json['user']),
        'course': !exists(json, 'course') ? undefined : CourseFromJSON(json['course']),
        'joinedAt': !exists(json, 'joinedAt') ? undefined : (new Date(json['joinedAt'])),
    };
}

export function UserCourseDataToJSON(value?: UserCourseData | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': UserCourseIdToJSON(value.id),
        'user': UserToJSON(value.user),
        'course': CourseToJSON(value.course),
        'joinedAt': value.joinedAt === undefined ? undefined : (value.joinedAt.toISOString()),
    };
}
