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
/**
 * 
 * @export
 * @interface ServiceModuleDetailsDto
 */
export interface ServiceModuleDetailsDto {
    /**
     * 
     * @type {number}
     * @memberof ServiceModuleDetailsDto
     */
    id: number;
    /**
     * 
     * @type {string}
     * @memberof ServiceModuleDetailsDto
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof ServiceModuleDetailsDto
     */
    instructionsFromTechnicalTeacher: string;
    /**
     * 
     * @type {string}
     * @memberof ServiceModuleDetailsDto
     */
    creatorName: string;
    /**
     * 
     * @type {string}
     * @memberof ServiceModuleDetailsDto
     */
    subject: string;
    /**
     * 
     * @type {Date}
     * @memberof ServiceModuleDetailsDto
     */
    creationTime: Date;
}

export function ServiceModuleDetailsDtoFromJSON(json: any): ServiceModuleDetailsDto {
    return ServiceModuleDetailsDtoFromJSONTyped(json, false);
}

export function ServiceModuleDetailsDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): ServiceModuleDetailsDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': json['id'],
        'name': json['name'],
        'instructionsFromTechnicalTeacher': json['instructionsFromTechnicalTeacher'],
        'creatorName': json['creatorName'],
        'subject': json['subject'],
        'creationTime': (new Date(json['creationTime'])),
    };
}

export function ServiceModuleDetailsDtoToJSON(value?: ServiceModuleDetailsDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
        'instructionsFromTechnicalTeacher': value.instructionsFromTechnicalTeacher,
        'creatorName': value.creatorName,
        'subject': value.subject,
        'creationTime': (value.creationTime.toISOString()),
    };
}
