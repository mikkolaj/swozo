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
    ActivitySummaryDto,
    ActivitySummaryDtoFromJSON,
    ActivitySummaryDtoFromJSONTyped,
    ActivitySummaryDtoToJSON,
} from './ActivitySummaryDto';
import {
    FileDto,
    FileDtoFromJSON,
    FileDtoFromJSONTyped,
    FileDtoToJSON,
} from './FileDto';

/**
 * 
 * @export
 * @interface FavouriteFileDto
 */
export interface FavouriteFileDto {
    /**
     * 
     * @type {FileDto}
     * @memberof FavouriteFileDto
     */
    file: FileDto;
    /**
     * 
     * @type {ActivitySummaryDto}
     * @memberof FavouriteFileDto
     */
    activitySummaryDto: ActivitySummaryDto;
}

export function FavouriteFileDtoFromJSON(json: any): FavouriteFileDto {
    return FavouriteFileDtoFromJSONTyped(json, false);
}

export function FavouriteFileDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): FavouriteFileDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'file': FileDtoFromJSON(json['file']),
        'activitySummaryDto': ActivitySummaryDtoFromJSON(json['activitySummaryDto']),
    };
}

export function FavouriteFileDtoToJSON(value?: FavouriteFileDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'file': FileDtoToJSON(value.file),
        'activitySummaryDto': ActivitySummaryDtoToJSON(value.activitySummaryDto),
    };
}

