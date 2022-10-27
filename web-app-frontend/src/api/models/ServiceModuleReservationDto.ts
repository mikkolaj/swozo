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
 * @interface ServiceModuleReservationDto
 */
export interface ServiceModuleReservationDto {
    /**
     * 
     * @type {number}
     * @memberof ServiceModuleReservationDto
     */
    reservationId: number;
    /**
     * 
     * @type {{ [key: string]: object; }}
     * @memberof ServiceModuleReservationDto
     */
    dynamicFieldAdditionalActions: { [key: string]: object; };
}

export function ServiceModuleReservationDtoFromJSON(json: any): ServiceModuleReservationDto {
    return ServiceModuleReservationDtoFromJSONTyped(json, false);
}

export function ServiceModuleReservationDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): ServiceModuleReservationDto {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'reservationId': json['reservationId'],
        'dynamicFieldAdditionalActions': json['dynamicFieldAdditionalActions'],
    };
}

export function ServiceModuleReservationDtoToJSON(value?: ServiceModuleReservationDto | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'reservationId': value.reservationId,
        'dynamicFieldAdditionalActions': value.dynamicFieldAdditionalActions,
    };
}
