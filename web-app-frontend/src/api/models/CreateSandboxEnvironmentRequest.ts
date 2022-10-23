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
 * @interface CreateSandboxEnvironmentRequest
 */
export interface CreateSandboxEnvironmentRequest {
    /**
     * 
     * @type {number}
     * @memberof CreateSandboxEnvironmentRequest
     */
    userCount: number;
}

export function CreateSandboxEnvironmentRequestFromJSON(json: any): CreateSandboxEnvironmentRequest {
    return CreateSandboxEnvironmentRequestFromJSONTyped(json, false);
}

export function CreateSandboxEnvironmentRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): CreateSandboxEnvironmentRequest {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'userCount': json['userCount'],
    };
}

export function CreateSandboxEnvironmentRequestToJSON(value?: CreateSandboxEnvironmentRequest | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'userCount': value.userCount,
    };
}

