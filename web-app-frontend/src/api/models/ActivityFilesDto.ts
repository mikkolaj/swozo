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
    FileDto,
    FileDtoFromJSON,
    FileDtoFromJSONTyped,
    FileDtoToJSON,
} from './FileDto';

/**
 * 
 * @export
 * @interface ActivityFilesDto
 */
export interface ActivityFilesDto {
    /**
     * 
     * @type {{ [key: string]: Array<FileDto>; }}
     * @memberof ActivityFilesDto
     */
    activityModuleIdToUserFiles: { [key: string]: Array<FileDto>; };
}

export function ActivityFilesDtoFromJSON(json: any): ActivityFilesDto {
    return ActivityFilesDtoFromJSONTyped(json, false);
}

export function ActivityFilesDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): ActivityFilesDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'activityModuleIdToUserFiles': json['activityModuleIdToUserFiles'],
    };
}

export function ActivityFilesDtoToJSON(value?: ActivityFilesDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'activityModuleIdToUserFiles': value.activityModuleIdToUserFiles,
    };
}

