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
 * @interface AddStudentRequest
 */
export interface AddStudentRequest {
    /**
     * 
     * @type {string}
     * @memberof AddStudentRequest
     */
    email: string;
}

export function AddStudentRequestFromJSON(json: any): AddStudentRequest {
    return AddStudentRequestFromJSONTyped(json, false);
}

export function AddStudentRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): AddStudentRequest {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'email': json['email'],
    };
}

export function AddStudentRequestToJSON(value?: AddStudentRequest | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'email': value.email,
    };
}
