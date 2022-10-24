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
 * @interface ActivityInstructionDto
 */
export interface ActivityInstructionDto {
    /**
     * 
     * @type {string}
     * @memberof ActivityInstructionDto
     */
    untrustedPossiblyDangerousHtml: string;
}

export function ActivityInstructionDtoFromJSON(json: any): ActivityInstructionDto {
    return ActivityInstructionDtoFromJSONTyped(json, false);
}

export function ActivityInstructionDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): ActivityInstructionDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'untrustedPossiblyDangerousHtml': json['untrustedPossiblyDangerousHtml'],
    };
}

export function ActivityInstructionDtoToJSON(value?: ActivityInstructionDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'untrustedPossiblyDangerousHtml': value.untrustedPossiblyDangerousHtml,
    };
}
