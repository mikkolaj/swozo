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
    Role,
    RoleFromJSON,
    RoleFromJSONTyped,
    RoleToJSON,
} from './Role';

/**
 * 
 * @export
 * @interface User
 */
export interface User {
    /**
     * 
     * @type {number}
     * @memberof User
     */
    id?: number;
    /**
     * 
     * @type {string}
     * @memberof User
     */
    name?: string;
    /**
     * 
     * @type {string}
     * @memberof User
     */
    surname?: string;
    /**
     * 
     * @type {string}
     * @memberof User
     */
    email?: string;
    /**
     * 
     * @type {Array<Role>}
     * @memberof User
     */
    roles?: Array<Role>;
}

export function UserFromJSON(json: any): User {
    return UserFromJSONTyped(json, false);
}

export function UserFromJSONTyped(json: any, ignoreDiscriminator: boolean): User {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': !exists(json, 'name') ? undefined : json['name'],
        'surname': !exists(json, 'surname') ? undefined : json['surname'],
        'email': !exists(json, 'email') ? undefined : json['email'],
        'roles': !exists(json, 'roles') ? undefined : ((json['roles'] as Array<any>).map(RoleFromJSON)),
    };
}

export function UserToJSON(value?: User | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
        'surname': value.surname,
        'email': value.email,
        'roles': value.roles === undefined ? undefined : ((value.roles as Array<any>).map(RoleToJSON)),
    };
}
