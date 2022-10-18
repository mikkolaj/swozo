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
 * @interface PrevalidateServiceModuleRequest
 */
export interface PrevalidateServiceModuleRequest {
    /**
     * 
     * @type {string}
     * @memberof PrevalidateServiceModuleRequest
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof PrevalidateServiceModuleRequest
     */
    instructionsFromTechnicalTeacher: string;
    /**
     * 
     * @type {string}
     * @memberof PrevalidateServiceModuleRequest
     */
    subject: string;
    /**
     * 
     * @type {string}
     * @memberof PrevalidateServiceModuleRequest
     */
    scheduleType: PrevalidateServiceModuleRequestScheduleTypeEnum;
    /**
     * 
     * @type {{ [key: string]: object; }}
     * @memberof PrevalidateServiceModuleRequest
     */
    dynamicProperties: { [key: string]: object; };
    /**
     * 
     * @type {boolean}
     * @memberof PrevalidateServiceModuleRequest
     */
    isPublic: boolean;
}

/**
* @export
* @enum {string}
*/
export enum PrevalidateServiceModuleRequestScheduleTypeEnum {
    Jupyter = 'JUPYTER',
    Docker = 'DOCKER'
}

export function PrevalidateServiceModuleRequestFromJSON(json: any): PrevalidateServiceModuleRequest {
    return PrevalidateServiceModuleRequestFromJSONTyped(json, false);
}

export function PrevalidateServiceModuleRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): PrevalidateServiceModuleRequest {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'name': json['name'],
        'instructionsFromTechnicalTeacher': json['instructionsFromTechnicalTeacher'],
        'subject': json['subject'],
        'scheduleType': json['scheduleType'],
        'dynamicProperties': json['dynamicProperties'],
        'isPublic': json['isPublic'],
    };
}

export function PrevalidateServiceModuleRequestToJSON(value?: PrevalidateServiceModuleRequest | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'name': value.name,
        'instructionsFromTechnicalTeacher': value.instructionsFromTechnicalTeacher,
        'subject': value.subject,
        'scheduleType': value.scheduleType,
        'dynamicProperties': value.dynamicProperties,
        'isPublic': value.isPublic,
    };
}
