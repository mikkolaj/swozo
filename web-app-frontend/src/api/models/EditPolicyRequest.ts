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
 * @interface EditPolicyRequest
 */
export interface EditPolicyRequest {
    /**
     * 
     * @type {string}
     * @memberof EditPolicyRequest
     */
    policyType?: EditPolicyRequestPolicyTypeEnum;
    /**
     * 
     * @type {number}
     * @memberof EditPolicyRequest
     */
    teacherId?: number;
    /**
     * 
     * @type {number}
     * @memberof EditPolicyRequest
     */
    value?: number;
}

/**
* @export
* @enum {string}
*/
export enum EditPolicyRequestPolicyTypeEnum {
    Vcpu = 'MAX_VCPU',
    RamGb = 'MAX_RAM_GB',
    DiskGb = 'MAX_DISK_GB',
    BandwidthMbps = 'MAX_BANDWIDTH_MBPS',
    Students = 'MAX_STUDENTS',
    ActivityDurationMinutes = 'MAX_ACTIVITY_DURATION_MINUTES',
    ParallelSandboxes = 'MAX_PARALLEL_SANDBOXES'
}

export function EditPolicyRequestFromJSON(json: any): EditPolicyRequest {
    return EditPolicyRequestFromJSONTyped(json, false);
}

export function EditPolicyRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): EditPolicyRequest {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'policyType': !exists(json, 'policyType') ? undefined : json['policyType'],
        'teacherId': !exists(json, 'teacherId') ? undefined : json['teacherId'],
        'value': !exists(json, 'value') ? undefined : json['value'],
    };
}

export function EditPolicyRequestToJSON(value?: EditPolicyRequest | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'policyType': value.policyType,
        'teacherId': value.teacherId,
        'value': value.value,
    };
}

