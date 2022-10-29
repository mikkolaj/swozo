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
    ActivityModuleDetailsDto,
    ActivityModuleDetailsDtoFromJSON,
    ActivityModuleDetailsDtoFromJSONTyped,
    ActivityModuleDetailsDtoToJSON,
} from './ActivityModuleDetailsDto';
import {
    FileDto,
    FileDtoFromJSON,
    FileDtoFromJSONTyped,
    FileDtoToJSON,
} from './FileDto';
import {
    InstructionDto,
    InstructionDtoFromJSON,
    InstructionDtoFromJSONTyped,
    InstructionDtoToJSON,
} from './InstructionDto';

/**
 * 
 * @export
 * @interface ActivityDetailsDto
 */
export interface ActivityDetailsDto {
    /**
     * 
     * @type {number}
     * @memberof ActivityDetailsDto
     */
    id: number;
    /**
     * 
     * @type {string}
     * @memberof ActivityDetailsDto
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof ActivityDetailsDto
     */
    description: string;
    /**
     * 
     * @type {Date}
     * @memberof ActivityDetailsDto
     */
    startTime: Date;
    /**
     * 
     * @type {Date}
     * @memberof ActivityDetailsDto
     */
    endTime: Date;
    /**
     * 
     * @type {InstructionDto}
     * @memberof ActivityDetailsDto
     */
    instructionFromTeacher: InstructionDto;
    /**
     * 
     * @type {Array<ActivityModuleDetailsDto>}
     * @memberof ActivityDetailsDto
     */
    activityModules: Array<ActivityModuleDetailsDto>;
    /**
     * 
     * @type {Array<FileDto>}
     * @memberof ActivityDetailsDto
     */
    publicFiles: Array<FileDto>;
}

export function ActivityDetailsDtoFromJSON(json: any): ActivityDetailsDto {
    return ActivityDetailsDtoFromJSONTyped(json, false);
}

export function ActivityDetailsDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): ActivityDetailsDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': json['id'],
        'name': json['name'],
        'description': json['description'],
        'startTime': (new Date(json['startTime'])),
        'endTime': (new Date(json['endTime'])),
        'instructionFromTeacher': InstructionDtoFromJSON(json['instructionFromTeacher']),
        'activityModules': ((json['activityModules'] as Array<any>).map(ActivityModuleDetailsDtoFromJSON)),
        'publicFiles': ((json['publicFiles'] as Array<any>).map(FileDtoFromJSON)),
    };
}

export function ActivityDetailsDtoToJSON(value?: ActivityDetailsDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
        'description': value.description,
        'startTime': (value.startTime.toISOString()),
        'endTime': (value.endTime.toISOString()),
        'instructionFromTeacher': InstructionDtoToJSON(value.instructionFromTeacher),
        'activityModules': ((value.activityModules as Array<any>).map(ActivityModuleDetailsDtoToJSON)),
        'publicFiles': ((value.publicFiles as Array<any>).map(FileDtoToJSON)),
    };
}

