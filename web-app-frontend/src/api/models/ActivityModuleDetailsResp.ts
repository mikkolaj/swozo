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
    ActivityLinkInfo,
    ActivityLinkInfoFromJSON,
    ActivityLinkInfoFromJSONTyped,
    ActivityLinkInfoToJSON,
} from './ActivityLinkInfo';
import {
    ServiceModuleDetailsResp,
    ServiceModuleDetailsRespFromJSON,
    ServiceModuleDetailsRespFromJSONTyped,
    ServiceModuleDetailsRespToJSON,
} from './ServiceModuleDetailsResp';

/**
 * 
 * @export
 * @interface ActivityModuleDetailsResp
 */
export interface ActivityModuleDetailsResp {
    /**
     * 
     * @type {number}
     * @memberof ActivityModuleDetailsResp
     */
    id: number;
    /**
     * 
     * @type {ServiceModuleDetailsResp}
     * @memberof ActivityModuleDetailsResp
     */
    module: ServiceModuleDetailsResp;
    /**
     * 
     * @type {string}
     * @memberof ActivityModuleDetailsResp
     */
    instruction: string;
    /**
     * 
     * @type {Array<ActivityLinkInfo>}
     * @memberof ActivityModuleDetailsResp
     */
    links: Array<ActivityLinkInfo>;
}

export function ActivityModuleDetailsRespFromJSON(json: any): ActivityModuleDetailsResp {
    return ActivityModuleDetailsRespFromJSONTyped(json, false);
}

export function ActivityModuleDetailsRespFromJSONTyped(json: any, ignoreDiscriminator: boolean): ActivityModuleDetailsResp {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': json['id'],
        'module': ServiceModuleDetailsRespFromJSON(json['module']),
        'instruction': json['instruction'],
        'links': ((json['links'] as Array<any>).map(ActivityLinkInfoFromJSON)),
    };
}

export function ActivityModuleDetailsRespToJSON(value?: ActivityModuleDetailsResp | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'module': ServiceModuleDetailsRespToJSON(value.module),
        'instruction': value.instruction,
        'links': ((value.links as Array<any>).map(ActivityLinkInfoToJSON)),
    };
}

