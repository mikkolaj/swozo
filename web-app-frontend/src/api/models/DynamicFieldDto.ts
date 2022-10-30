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
    ParameterDescription,
    ParameterDescriptionFromJSON,
    ParameterDescriptionFromJSONTyped,
    ParameterDescriptionToJSON,
} from './ParameterDescription';

/**
 * 
 * @export
 * @interface DynamicFieldDto
 */
export interface DynamicFieldDto {
    /**
     * 
     * @type {string}
     * @memberof DynamicFieldDto
     */
    value: string;
    /**
     * 
     * @type {ParameterDescription}
     * @memberof DynamicFieldDto
     */
    parameterDescription: ParameterDescription;
}

export function DynamicFieldDtoFromJSON(json: any): DynamicFieldDto {
    return DynamicFieldDtoFromJSONTyped(json, false);
}

export function DynamicFieldDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): DynamicFieldDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'value': json['value'],
        'parameterDescription': ParameterDescriptionFromJSON(json['parameterDescription']),
    };
}

export function DynamicFieldDtoToJSON(value?: DynamicFieldDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'value': value.value,
        'parameterDescription': ParameterDescriptionToJSON(value.parameterDescription),
    };
}
