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


import * as runtime from '../runtime';
import {
    ExampleModel,
    ExampleModelFromJSON,
    ExampleModelToJSON,
} from '../models';

/**
 * 
 */
export class ExampleControllerApi extends runtime.BaseAPI {

    /**
     */
    async getExampleRaw(initOverrides?: RequestInit): Promise<runtime.ApiResponse<string>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            const token = this.configuration.accessToken;
            const tokenString = await token("JWT_AUTH", []);

            if (tokenString) {
                headerParameters["Authorization"] = `Bearer ${tokenString}`;
            }
        }
        const response = await this.request({
            path: `/example`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.TextApiResponse(response) as any;
    }

    /**
     */
    async getExample(initOverrides?: RequestInit): Promise<string> {
        const response = await this.getExampleRaw(initOverrides);
        return await response.value();
    }

    /**
     */
    async getExampleErrorRaw(initOverrides?: RequestInit): Promise<runtime.ApiResponse<void>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            const token = this.configuration.accessToken;
            const tokenString = await token("JWT_AUTH", []);

            if (tokenString) {
                headerParameters["Authorization"] = `Bearer ${tokenString}`;
            }
        }
        const response = await this.request({
            path: `/example/err`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.VoidApiResponse(response);
    }

    /**
     */
    async getExampleError(initOverrides?: RequestInit): Promise<void> {
        await this.getExampleErrorRaw(initOverrides);
    }

    /**
     */
    async getExampleJsonRaw(initOverrides?: RequestInit): Promise<runtime.ApiResponse<ExampleModel>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            const token = this.configuration.accessToken;
            const tokenString = await token("JWT_AUTH", []);

            if (tokenString) {
                headerParameters["Authorization"] = `Bearer ${tokenString}`;
            }
        }
        const response = await this.request({
            path: `/example/json`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => ExampleModelFromJSON(jsonValue));
    }

    /**
     */
    async getExampleJson(initOverrides?: RequestInit): Promise<ExampleModel> {
        const response = await this.getExampleJsonRaw(initOverrides);
        return await response.value();
    }

}