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
    InstructionDto,
    InstructionDtoFromJSON,
    InstructionDtoFromJSONTyped,
    InstructionDtoToJSON,
} from './InstructionDto';
import {
    ServiceModuleMdaDto,
    ServiceModuleMdaDtoFromJSON,
    ServiceModuleMdaDtoFromJSONTyped,
    ServiceModuleMdaDtoToJSON,
} from './ServiceModuleMdaDto';

/**
 * 
 * @export
 * @interface ReserveServiceModuleRequest
 */
export interface ReserveServiceModuleRequest {
    /**
     * 
     * @type {string}
     * @memberof ReserveServiceModuleRequest
     */
    name: string;
    /**
     * 
     * @type {string}
     * @memberof ReserveServiceModuleRequest
     */
    subject: string;
    /**
     * 
     * @type {string}
     * @memberof ReserveServiceModuleRequest
     */
    description: string;
    /**
     * 
     * @type {InstructionDto}
     * @memberof ReserveServiceModuleRequest
     */
    teacherInstruction: InstructionDto;
    /**
     * 
     * @type {InstructionDto}
     * @memberof ReserveServiceModuleRequest
     */
    studentInstruction: InstructionDto;
    /**
     * 
     * @type {string}
     * @memberof ReserveServiceModuleRequest
     */
    serviceName: string;
    /**
     * 
     * @type {{ [key: string]: string; }}
     * @memberof ReserveServiceModuleRequest
     */
    dynamicProperties: { [key: string]: string; };
    /**
     * 
     * @type {boolean}
     * @memberof ReserveServiceModuleRequest
     */
    isPublic: boolean;
    /**
     * 
     * @type {ServiceModuleMdaDto}
     * @memberof ReserveServiceModuleRequest
     */
    mdaData: ServiceModuleMdaDto;
}

export function ReserveServiceModuleRequestFromJSON(json: any): ReserveServiceModuleRequest {
    return ReserveServiceModuleRequestFromJSONTyped(json, false);
}

export function ReserveServiceModuleRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): ReserveServiceModuleRequest {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'name': json['name'],
        'subject': json['subject'],
        'description': json['description'],
        'teacherInstruction': InstructionDtoFromJSON(json['teacherInstruction']),
        'studentInstruction': InstructionDtoFromJSON(json['studentInstruction']),
        'serviceName': json['serviceName'],
        'dynamicProperties': json['dynamicProperties'],
        'isPublic': json['isPublic'],
        'mdaData': ServiceModuleMdaDtoFromJSON(json['mdaData']),
    };
}

export function ReserveServiceModuleRequestToJSON(value?: ReserveServiceModuleRequest | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'name': value.name,
        'subject': value.subject,
        'description': value.description,
        'teacherInstruction': InstructionDtoToJSON(value.teacherInstruction),
        'studentInstruction': InstructionDtoToJSON(value.studentInstruction),
        'serviceName': value.serviceName,
        'dynamicProperties': value.dynamicProperties,
        'isPublic': value.isPublic,
        'mdaData': ServiceModuleMdaDtoToJSON(value.mdaData),
    };
}

