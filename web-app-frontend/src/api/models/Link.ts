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
 * @interface Link
 */
export interface Link {
    /**
     * 
     * @type {string}
     * @memberof Link
     */
    href?: string;
    /**
     * 
     * @type {string}
     * @memberof Link
     */
    hreflang?: string;
    /**
     * 
     * @type {string}
     * @memberof Link
     */
    title?: string;
    /**
     * 
     * @type {string}
     * @memberof Link
     */
    type?: string;
    /**
     * 
     * @type {string}
     * @memberof Link
     */
    deprecation?: string;
    /**
     * 
     * @type {string}
     * @memberof Link
     */
    profile?: string;
    /**
     * 
     * @type {string}
     * @memberof Link
     */
    name?: string;
    /**
     * 
     * @type {boolean}
     * @memberof Link
     */
    templated?: boolean;
}

export function LinkFromJSON(json: any): Link {
    return LinkFromJSONTyped(json, false);
}

export function LinkFromJSONTyped(json: any, ignoreDiscriminator: boolean): Link {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'href': !exists(json, 'href') ? undefined : json['href'],
        'hreflang': !exists(json, 'hreflang') ? undefined : json['hreflang'],
        'title': !exists(json, 'title') ? undefined : json['title'],
        'type': !exists(json, 'type') ? undefined : json['type'],
        'deprecation': !exists(json, 'deprecation') ? undefined : json['deprecation'],
        'profile': !exists(json, 'profile') ? undefined : json['profile'],
        'name': !exists(json, 'name') ? undefined : json['name'],
        'templated': !exists(json, 'templated') ? undefined : json['templated'],
    };
}

export function LinkToJSON(value?: Link | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'href': value.href,
        'hreflang': value.hreflang,
        'title': value.title,
        'type': value.type,
        'deprecation': value.deprecation,
        'profile': value.profile,
        'name': value.name,
        'templated': value.templated,
    };
}

