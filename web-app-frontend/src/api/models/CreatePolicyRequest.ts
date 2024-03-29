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
 * @interface CreatePolicyRequest
 */
export interface CreatePolicyRequest {
    /**
     * 
     * @type {string}
     * @memberof CreatePolicyRequest
     */
    policyType: CreatePolicyRequestPolicyTypeEnum;
    /**
     * 
     * @type {number}
     * @memberof CreatePolicyRequest
     */
    teacherId: number;
    /**
     * 
     * @type {number}
     * @memberof CreatePolicyRequest
     */
    value: number;
}

/**
* @export
* @enum {string}
*/
export enum CreatePolicyRequestPolicyTypeEnum {
    Vcpu = 'MAX_VCPU',
    RamGb = 'MAX_RAM_GB',
    DiskGb = 'MAX_DISK_GB',
    BandwidthMbps = 'MAX_BANDWIDTH_MBPS',
    Students = 'MAX_STUDENTS',
    ActivityDurationMinutes = 'MAX_ACTIVITY_DURATION_MINUTES',
    ParallelSandboxes = 'MAX_PARALLEL_SANDBOXES'
}

export function CreatePolicyRequestFromJSON(json: any): CreatePolicyRequest {
    return CreatePolicyRequestFromJSONTyped(json, false);
}

export function CreatePolicyRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): CreatePolicyRequest {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'policyType': json['policyType'],
        'teacherId': json['teacherId'],
        'value': json['value'],
    };
}

export function CreatePolicyRequestToJSON(value?: CreatePolicyRequest | null): any {
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

