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
    CourseSummaryDto,
    CourseSummaryDtoFromJSON,
    CourseSummaryDtoFromJSONTyped,
    CourseSummaryDtoToJSON,
} from './CourseSummaryDto';
import {
    PolicyDto,
    PolicyDtoFromJSON,
    PolicyDtoFromJSONTyped,
    PolicyDtoToJSON,
} from './PolicyDto';
import {
    ServiceModuleSummaryDto,
    ServiceModuleSummaryDtoFromJSON,
    ServiceModuleSummaryDtoFromJSONTyped,
    ServiceModuleSummaryDtoToJSON,
} from './ServiceModuleSummaryDto';

/**
 * 
 * @export
 * @interface UserAdminDetailsDto
 */
export interface UserAdminDetailsDto {
    /**
     * 
     * @type {number}
     * @memberof UserAdminDetailsDto
     */
    id: number;
    /**
     * 
     * @type {string}
     * @memberof UserAdminDetailsDto
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof UserAdminDetailsDto
     */
    surname: string;
    /**
     * 
     * @type {string}
     * @memberof UserAdminDetailsDto
     */
    email: string;
    /**
     * 
     * @type {Array<string>}
     * @memberof UserAdminDetailsDto
     */
    roles: Array<UserAdminDetailsDtoRolesEnum>;
    /**
     * 
     * @type {Date}
     * @memberof UserAdminDetailsDto
     */
    createdAt: Date;
    /**
     * 
     * @type {number}
     * @memberof UserAdminDetailsDto
     */
    storageUsageBytes: number;
    /**
     * 
     * @type {Array<CourseSummaryDto>}
     * @memberof UserAdminDetailsDto
     */
    attendedCourses: Array<CourseSummaryDto>;
    /**
     * 
     * @type {Array<CourseSummaryDto>}
     * @memberof UserAdminDetailsDto
     */
    createdCourses: Array<CourseSummaryDto>;
    /**
     * 
     * @type {Array<ServiceModuleSummaryDto>}
     * @memberof UserAdminDetailsDto
     */
    createdModules: Array<ServiceModuleSummaryDto>;
    /**
     * 
     * @type {Array<PolicyDto>}
     * @memberof UserAdminDetailsDto
     */
    userPolicies: Array<PolicyDto>;
}

/**
* @export
* @enum {string}
*/
export enum UserAdminDetailsDtoRolesEnum {
    Student = 'STUDENT',
    Teacher = 'TEACHER',
    TechnicalTeacher = 'TECHNICAL_TEACHER',
    Admin = 'ADMIN'
}

export function UserAdminDetailsDtoFromJSON(json: any): UserAdminDetailsDto {
    return UserAdminDetailsDtoFromJSONTyped(json, false);
}

export function UserAdminDetailsDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): UserAdminDetailsDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': json['id'],
        'name': json['name'],
        'surname': json['surname'],
        'email': json['email'],
        'roles': json['roles'],
        'createdAt': (new Date(json['createdAt'])),
        'storageUsageBytes': json['storageUsageBytes'],
        'attendedCourses': ((json['attendedCourses'] as Array<any>).map(CourseSummaryDtoFromJSON)),
        'createdCourses': ((json['createdCourses'] as Array<any>).map(CourseSummaryDtoFromJSON)),
        'createdModules': ((json['createdModules'] as Array<any>).map(ServiceModuleSummaryDtoFromJSON)),
        'userPolicies': ((json['userPolicies'] as Array<any>).map(PolicyDtoFromJSON)),
    };
}

export function UserAdminDetailsDtoToJSON(value?: UserAdminDetailsDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
        'surname': value.surname,
        'email': value.email,
        'roles': value.roles,
        'createdAt': (value.createdAt.toISOString()),
        'storageUsageBytes': value.storageUsageBytes,
        'attendedCourses': ((value.attendedCourses as Array<any>).map(CourseSummaryDtoToJSON)),
        'createdCourses': ((value.createdCourses as Array<any>).map(CourseSummaryDtoToJSON)),
        'createdModules': ((value.createdModules as Array<any>).map(ServiceModuleSummaryDtoToJSON)),
        'userPolicies': ((value.userPolicies as Array<any>).map(PolicyDtoToJSON)),
    };
}

