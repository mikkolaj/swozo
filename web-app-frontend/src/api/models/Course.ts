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
    Activity,
    ActivityFromJSON,
    ActivityFromJSONTyped,
    ActivityToJSON,
} from './Activity';
import {
    User,
    UserFromJSON,
    UserFromJSONTyped,
    UserToJSON,
} from './User';
import {
    UserCourseData,
    UserCourseDataFromJSON,
    UserCourseDataFromJSONTyped,
    UserCourseDataToJSON,
} from './UserCourseData';

/**
 * 
 * @export
 * @interface Course
 */
export interface Course {
    /**
     * 
     * @type {number}
     * @memberof Course
     */
    id?: number;
    /**
     * 
     * @type {string}
     * @memberof Course
     */
    name?: string;
    /**
     * 
     * @type {string}
     * @memberof Course
     */
    subject?: string;
    /**
     * 
     * @type {string}
     * @memberof Course
     */
    description?: string;
    /**
     * 
     * @type {string}
     * @memberof Course
     */
    joinUUID?: string;
    /**
     * 
     * @type {string}
     * @memberof Course
     */
    password?: string;
    /**
     * 
     * @type {Date}
     * @memberof Course
     */
    creationTime?: Date;
    /**
     * 
     * @type {Array<Activity>}
     * @memberof Course
     */
    activities?: Array<Activity>;
    /**
     * 
     * @type {Array<UserCourseData>}
     * @memberof Course
     */
    students?: Array<UserCourseData>;
    /**
     * 
     * @type {User}
     * @memberof Course
     */
    teacher?: User;
}

export function CourseFromJSON(json: any): Course {
    return CourseFromJSONTyped(json, false);
}

export function CourseFromJSONTyped(json: any, ignoreDiscriminator: boolean): Course {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': !exists(json, 'name') ? undefined : json['name'],
        'subject': !exists(json, 'subject') ? undefined : json['subject'],
        'description': !exists(json, 'description') ? undefined : json['description'],
        'joinUUID': !exists(json, 'joinUUID') ? undefined : json['joinUUID'],
        'password': !exists(json, 'password') ? undefined : json['password'],
        'creationTime': !exists(json, 'creationTime') ? undefined : (new Date(json['creationTime'])),
        'activities': !exists(json, 'activities') ? undefined : ((json['activities'] as Array<any>).map(ActivityFromJSON)),
        'students': !exists(json, 'students') ? undefined : ((json['students'] as Array<any>).map(UserCourseDataFromJSON)),
        'teacher': !exists(json, 'teacher') ? undefined : UserFromJSON(json['teacher']),
    };
}

export function CourseToJSON(value?: Course | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
        'subject': value.subject,
        'description': value.description,
        'joinUUID': value.joinUUID,
        'password': value.password,
        'creationTime': value.creationTime === undefined ? undefined : (value.creationTime.toISOString()),
        'activities': value.activities === undefined ? undefined : ((value.activities as Array<any>).map(ActivityToJSON)),
        'students': value.students === undefined ? undefined : ((value.students as Array<any>).map(UserCourseDataToJSON)),
        'teacher': UserToJSON(value.teacher),
    };
}

